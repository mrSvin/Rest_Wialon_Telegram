package SpringWialonApplication.service;

import SpringWialonApplication.api.response.WabcoReportResponse;
import SpringWialonApplication.repository.TrailersRepository;
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

    private OkHttpClient client;
    private Request request;
    Response response;

    private final TrailersRepository trailersRepository;

    private ServiceWialon(TrailersRepository trailersRepository) {
        this.trailersRepository = trailersRepository;
    }

    public String writeData() {

        List<List> dataWialon = findArrayNameIdWialon();

        for (int i = 0; i < dataWialon.get(0).size(); i++) {

            if (trailersRepository.countWialonId(dataWialon.get(1).get(i)) == 0) {
                trailersRepository.addWialonData(dataWialon.get(0).get(i), dataWialon.get(1).get(i));
            }
        }

        return "ok";
    }

    private List<List> findArrayNameIdWialon() {

        if (uid.equals("")) {
            uid = findEid();
        }

        client = new OkHttpClient();
        request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":1,\"from\":0,\"to\":0}&sid=" + uid)
                .method("GET", null)
                .build();

        response = null;
        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());

            //Получаяем всё сообщение запроса
            JSONObject jsonResponse = new JSONObject(response.body().string());
//            System.out.println(jsonResponse);
            JSONArray items = jsonResponse.getJSONArray("items");


            List<String> names = new ArrayList<>();
            List<Integer> id = new ArrayList<>();
            if (items != null) {
                int len = items.length();
                for (int i = 0; i < len; i++) {
                    JSONObject indexItem = (JSONObject) items.get(i);

                    names.add(indexItem.get("nm").toString());
                    id.add(Integer.valueOf(indexItem.get("id").toString()));
                }
            }

            List<List> result = new ArrayList<>();
            result.add(names);
            result.add(id);

            return result;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            uid = "";
            return null;
        }

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

    public Iterable infoData() {
        return trailersRepository.findAll();
    }

    public int countData() {
        return trailersRepository.countWialonTrailers();
    }

    public String infoObject(int idObject) {

        if (uid.equals("")) {
            uid = findEid();
        }

        client = new OkHttpClient();
        request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=core/search_item&params={\"id\":" + idObject + ",\"flags\":1025}&sid=" + uid)
                .method("GET", null)
                .build();

        response = null;

        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            JSONObject jsonResponse = new JSONObject(response.body().string());

            return jsonResponse.getString("item");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            uid = "";
            return null;
        }

    }

    public String reportBase(int idObject, int startTime, int lastTime) {

        if (uid.equals("")) {
            uid = findEid();
        }

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
            e.printStackTrace();
            uid = "";
            return null;
        }

    }

    public WabcoReportResponse reportWabco(int idObject, int startTime, int lastTime) {

        WabcoReportResponse wabcoReportResponse = new WabcoReportResponse();

        if (uid.equals("")) {
            uid = findEid();
        }

        try {

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
            wabcoReportResponse.setPressuareSystem(reportWabco(0));
            //Заполняем напряжение
            wabcoReportResponse.setVoltage(reportWabco(5));
            //Заполняем позиции
            wabcoReportResponse.setPossitions(reportPositions(5));
            //Заполняем давление в колесах
            wabcoReportResponse.setPressuareWheels(reportListWabco(4));
            //Заполняем скорость
            wabcoReportResponse.setSpeed(reportWabco(7));

            return wabcoReportResponse;

        } catch (IOException e) {
            e.printStackTrace();
            uid = "";
            return wabcoReportResponse;
        }

    }

    private String reportWabco(int indexReport) {
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

            JSONObject data = datasets.getJSONObject("0").getJSONObject("data");
            return data.toString();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String reportPositions(int indexReport) {
        try {
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




}
