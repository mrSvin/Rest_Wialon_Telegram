package SpringWialonApplication.service;

import SpringWialonApplication.api.response.WabcoReportResponse;
import SpringWialonApplication.repository.TrailersRepository;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceWialon {

    @Value("${token}")
    private String token;
    private String uid = "";
    private boolean trigerCheckUid = true;

    private OkHttpClient client;
    private Request request;
    Response response;
    long unixTime;

    String itemsCache = "";
    long unixCashTime;

    private final TrailersRepository trailersRepository;

    private ServiceWialon(TrailersRepository trailersRepository) {
        this.trailersRepository = trailersRepository;
    }

    private String findEid() {

        client = new OkHttpClient();
        request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=token/login&params={\"token\":\"" + token + "\"}")
                .method("GET", null)
                .build();

        response = null;
        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());

            //Получаяем всё сообщение запроса
            JSONObject jsonResponse = new JSONObject(response.body().string());
//            System.out.println(jsonResponse);
            String eid = jsonResponse.getString("eid");
            System.out.println("eid:" + eid);
            return eid;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String findALlTrailersWialon() {

        checkUpdateUid();

        long timeNow = System.currentTimeMillis() / 1000L;
        if (timeNow - unixCashTime > 600 || itemsCache.equals("")) {
            unixCashTime = timeNow;
            System.out.println("Выполнен новый запрос на получение списка ППЦ");
            try {
                client = new OkHttpClient();
                request = new Request.Builder()
                        .url("https://hst-api.wialon.com/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":9501,\"from\":0,\"to\":0}&sid=" + uid)
                        .method("GET", null)
                        .build();

                response = null;

                response = client.newCall(request).execute();
                JSONObject jsonResponse = new JSONObject(response.body().string());
                itemsCache = jsonResponse.getString("items");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                uid = "";
                return null;
            }

        } else {
            System.out.println("Получение списка ППЦ с кэша");
        }

        return itemsCache;

    }


    public int countData() {
        return trailersRepository.countWialonTrailers();
    }

    public String infoObject(int idObject) {

        checkUpdateUid();
        System.out.println("Выполнен запрос о получении информации объекта " + idObject);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=core/search_item&params={\"id\":" + idObject + ",\"flags\":1025}&sid=" + uid)
                .method("GET", null)
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            JSONObject jsonResponse = new JSONObject(response.body().string());

            return jsonResponse.getString("item");

        } catch (IOException | JSONException e) {
//            e.printStackTrace();
            System.out.println("error object:" + idObject);
            uid = "";
            return null;
        }

    }

    public String reportBase(int idObject, int startTime, int lastTime) {

        checkUpdateUid();

        client = new OkHttpClient();
        request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":17376348," +
                        "\"reportTemplateId\":1,\"reportObjectId\":" + idObject + ",\"reportObjectSecId\":0,\"interval\":" +
                        "{\"from\":" + startTime + ",\"to\":" + lastTime + ",\"flags\":0}}&sid=" + uid)
                .method("GET", null)
                .build();

        response = null;

        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            JSONObject jsonResponse = new JSONObject(response.body().string());

            JSONObject jsonObject = jsonResponse.getJSONObject("reportResult");

            return jsonObject.getString("stats");

        } catch (IOException | JSONException e) {
//            e.printStackTrace();
            System.out.println("error object:" + idObject);
            uid = "";
            return null;
        }

    }

    public WabcoReportResponse reportWabco(int idObject, int startTime, int lastTime) {

        WabcoReportResponse wabcoReportResponse = new WabcoReportResponse();

        try {
            checkUpdateUid();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":17376348," +
                            "\"reportTemplateId\":11,\"reportObjectId\":" + idObject + ",\"reportObjectSecId\":0,\"interval\":" +
                            "{\"from\":" + startTime + ",\"to\":" + lastTime + ",\"flags\":0}}&sid=" + uid)
                    .method("GET", null)
                    .build();

            client.newCall(request).execute();

            request = new Request.Builder()
                    .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/get_result_chart&params=" +
                            "{\"attachmentIndex\":3,\"action\":0,\"width\":1342,\"height\":201,\"autoScaleY\":0,\"pixelFrom\":0," +
                            "\"pixelTo\":0,\"flags\":4356}&sid=" + uid)
                    .method("GET", null)
                    .build();

            client.newCall(request).execute();

            //Заполняем нагрузку на оси
            wabcoReportResponse.setLoadAxle(reportListWabco(3));
            //Заполняем давление в пневмосистеме
            wabcoReportResponse.setPressuareSystem(reportIndexWabco(0));
            //Заполняем напряжение
            wabcoReportResponse.setVoltage(reportIndexWabco(5));
            //Заполняем позиции
            wabcoReportResponse.setPossitions(reportPositions(5));
            //Заполняем давление в колесах
            wabcoReportResponse.setPressuareWheels(reportListWabco(4));
            //Заполняем скорость
            wabcoReportResponse.setSpeed(reportIndexWabco(7));

            return wabcoReportResponse;

        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Ошибка в выполнении запроса: " + idObject + " " + startTime + " " + lastTime);
            uid = "";
            return wabcoReportResponse;
        }

    }

    private String reportIndexWabco(int indexReport) {

        try {
            checkUpdateUid();
            Request request = new Request.Builder()
                    .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/render_json&params=" +
                            "{\"attachmentIndex\":" + indexReport + ",\"width\":1342,\"useCrop\":1," +
                            "\"cropBegin\":1663920741,\"cropEnd\":1663932741}&sid=" + uid)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONObject datasets = jsonResponse.getJSONObject("datasets");

            JSONObject data = datasets.getJSONObject("0").getJSONObject("data");
            return data.toString();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String reportPositions(int indexReport) {

        try {
            checkUpdateUid();
            Request request = new Request.Builder()
                    .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/render_json&params=" +
                            "{\"attachmentIndex\":" + indexReport + ",\"width\":1342,\"useCrop\":1," +
                            "\"cropBegin\":1663920741,\"cropEnd\":1663932741}&sid=" + uid)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject jsonResponse = new JSONObject(response.body().string());

            return jsonResponse.getJSONObject("possitions").toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private List<String> reportListWabco(int indexReport) {
        try {

            Request request = new Request.Builder()
                    .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/render_json&params=" +
                            "{\"attachmentIndex\":" + indexReport + ",\"width\":1342,\"useCrop\":1," +
                            "\"cropBegin\":1663920741,\"cropEnd\":1663932741}&sid=" + uid)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONObject datasets = jsonResponse.getJSONObject("datasets");

            List<String> result = new ArrayList<>();
            for (int i = 0; i < datasets.length(); i++) {
                JSONObject data = datasets.getJSONObject(String.valueOf(i));
                result.add(data.getString("data"));
            }

            return result;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @SneakyThrows
    private void checkUpdateUid() {

        if (trigerCheckUid) {
            trigerCheckUid=false;
            long timeNow = System.currentTimeMillis() / 1000L;
            if (timeNow - unixTime > 600) {
                uid = findEid();
                unixTime = timeNow;
                System.out.println("Выполнен запрос uid в связи с превышением времени");
            }

            if (uid.equals("")) {
                uid = findEid();
                unixTime = timeNow;
                System.out.println("Выполнен запрос uid в связи с его отсутствием");
            }
            trigerCheckUid=true;
        } else {
            Thread.sleep(100);
        }

    }


}
