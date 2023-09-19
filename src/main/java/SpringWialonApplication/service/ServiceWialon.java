package SpringWialonApplication.service;

import SpringWialonApplication.api.response.WabcoReportResponse;
import lombok.SneakyThrows;
import lombok.Synchronized;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ServiceWialon {

    @Value("${token}")
    private String token;
    private String uid = "";
    private boolean trigerCheckUid = true;

    long unixTime = 20000;

    String itemsCache = "";
    long unixCashTime = 0;

    private final JwtService jwtService;

    private ServiceWialon(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private String findEid() throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(3, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=token/login&params={\"token\":\"" + token + "\"}")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        try {

            JSONObject jsonResponse = new JSONObject(response.body().string());
            String eid = jsonResponse.getString("eid");
            System.out.println("eid:" + eid);
            return eid;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public String findAllTrailersWialon(String jwtToken) {

        String checkJwt = jwtService.checkJWT(jwtToken, "all");
        if (checkJwt.equals("error")) {
            return "invalid token";
        } else if (checkJwt.equals("no rights")) {
            return "no rights";
        }

        checkUpdateUid();
        if (uid.length() < 5) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long timeNow = System.currentTimeMillis() / 1000L;
        if (timeNow - unixCashTime > 600 || itemsCache.equals("")) {
            unixCashTime = timeNow;
            System.out.println("Выполнен новый запрос на получение списка ППЦ");
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://hst-api.wialon.com/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":9501,\"from\":0,\"to\":0}&sid=" + uid)
                        .method("GET", null)
                        .build();

                System.out.println("uid findALlTrailersWialon: " + uid);
                Response response = client.newCall(request).execute();
                JSONObject jsonResponse = new JSONObject(response.body().string());
                itemsCache = jsonResponse.getString("items");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            System.out.println("Получение списка ППЦ с кэша");
        }

        return itemsCache;

    }

    public String infoObject(int idObject, String jwtToken) {

        String checkJwt = jwtService.checkJWT(jwtToken, "all");
        if (checkJwt.equals("error")) {
            return "invalid token";
        } else if (checkJwt.equals("no rights")) {
            return "no rights";
        }

        checkUpdateUid();
        if (uid.length() < 5) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Выполнен запрос о получении информации объекта " + idObject);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=core/search_item&params={\"id\":" + idObject + ",\"flags\":1025}&sid=" + uid)
                .method("GET", null)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            JSONObject jsonResponse = new JSONObject(response.body().string());
            System.out.println("uid infoObject: " + uid);
            return jsonResponse.getString("item");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.println("error object:" + idObject);
            return null;
        }

    }

    public String reportBase(int idObject, int startTime, int lastTime, String jwtToken) throws IOException {

        String checkJwt = jwtService.checkJWT(jwtToken, "all");
        if (checkJwt.equals("error")) {
            return "invalid token";
        } else if (checkJwt.equals("no rights")) {
            return "no rights";
        }

        checkUpdateUid();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":17376348," +
                        "\"reportTemplateId\":1,\"reportObjectId\":" + idObject + ",\"reportObjectSecId\":0,\"interval\":" +
                        "{\"from\":" + startTime + ",\"to\":" + lastTime + ",\"flags\":0}}&sid=" + uid)
                .method("GET", null)
                .build();

        Response response = client.newCall(request).execute();
        try {

//            System.out.println(response.body().string());
            JSONObject jsonResponse = new JSONObject(response.body().string());

            JSONObject jsonObject = jsonResponse.getJSONObject("reportResult");

            return jsonObject.getString("stats");

        } catch (IOException | JSONException e) {
//            e.printStackTrace();
            System.out.println("error object:" + idObject);
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public WabcoReportResponse reportWabco(int idObject, int startTime, int lastTime, String jwtToken) throws IOException {

        WabcoReportResponse wabcoReportResponse = new WabcoReportResponse();

        String checkJwt = jwtService.checkJWT(jwtToken, "all");
        if (checkJwt.equals("error")) {
            wabcoReportResponse.setError("invalid token");
            return wabcoReportResponse;
        } else if (checkJwt.equals("no rights")) {
            wabcoReportResponse.setError("no rights");
            return wabcoReportResponse;
        }

        int period = lastTime - startTime;
        if (period > 2678400) {
            wabcoReportResponse.setError("period more than 31 days");
            return wabcoReportResponse;
        }
        System.out.println(period);

        checkUpdateUid();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":17376348," +
                        "\"reportTemplateId\":15,\"reportObjectId\":" + idObject + ",\"reportObjectSecId\":0,\"interval\":" +
                        "{\"from\":" + startTime + ",\"to\":" + lastTime + ",\"flags\":0}}&sid=" + uid)
                .build();

        Response response = client.newCall(request).execute();

        try {

            //Заполняем напряжение
            wabcoReportResponse.setVoltage(reportIndexWabco(0, startTime, lastTime, "voltage"));
            //Заполняем позиции
            wabcoReportResponse.setPossitionsX(reportIndexWabco(0, startTime, lastTime, "possitions_x"));
            wabcoReportResponse.setPossitionsY(reportIndexWabco(0, startTime, lastTime, "possitions_y"));
            //Заполняем скорость
            wabcoReportResponse.setSpeed(reportIndexWabco(1, startTime, lastTime, "speed"));
            //Заполняем время
            wabcoReportResponse.setTime(reportIndexWabco(0, startTime, lastTime, "time"));
            //error nil
            wabcoReportResponse.setError("null");

            return wabcoReportResponse;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка в выполнении запроса: " + idObject + " " + startTime + " " + lastTime);
            return wabcoReportResponse;
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    private String reportIndexWabco(int indexReport, int startTime, int lastTime, String param) throws IOException {

        checkUpdateUid();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://hst-api.wialon.com/wialon/ajax.html?svc=report/render_json&params=" +
                        "{\"attachmentIndex\":" + indexReport + ",\"width\":1342,\"useCrop\":1," +
                        "\"cropBegin\":" + startTime + ",\"cropEnd\":" + lastTime + "}&sid=" + uid)
                .method("GET", null)
                .build();

        Response response = client.newCall(request).execute();

        try {

            JSONObject jsonResponse = new JSONObject(response.body().string());

            if (param == "possitions_x") {
                JSONObject datasets = jsonResponse.getJSONObject("possitions");
                return String.valueOf(datasets.getJSONArray("lon"));
            }
            if (param == "possitions_y") {
                JSONObject datasets = jsonResponse.getJSONObject("possitions");
                return String.valueOf(datasets.getJSONArray("lat"));
            }
            if (param == "speed") {
                return String.valueOf(jsonResponse.getJSONObject("datasets").getJSONObject("0").getJSONObject("data").getJSONArray("y"));
            }
            if (param == "voltage") {
                return String.valueOf(jsonResponse.getJSONObject("datasets").getJSONObject("0").getJSONObject("data").getJSONArray("y"));
            }
            if (param == "time") {
                return String.valueOf(jsonResponse.getJSONObject("datasets").getJSONObject("0").getJSONObject("data").getJSONArray("x"));
            }

            return null;
        } catch (JSONException | IOException e) {
//            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }


    @SneakyThrows
    @Synchronized
    //обновлям uid только раз в определенный период, делаем синхронизацию чтобы uid не обновляли несколько потоков сразу
    private void checkUpdateUid() {
        long timeNow = System.currentTimeMillis() / 1000L;
        long timeLast = timeNow - unixTime;
        System.out.println("Пройденное время с последнего запроса uid: " + timeLast);
        if (timeLast > 420 || uid.equals("")) {
            unixTime = timeNow;
            System.out.println("Выполнен новый запрос uid");
            uid = findEid();
        }

    }


}
