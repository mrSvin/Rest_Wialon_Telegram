package SpringWialonApplication.service;

import ch.qos.logback.core.net.server.Client;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;

@Service
public class ServiceParcer {

    public StringBuffer objectInfo(String input) {
        StringBuffer result = new StringBuffer();
        try {

            JSONObject obj = new JSONObject(input);
            JSONObject pos = obj.getJSONObject("pos");
            JSONObject p = obj.getJSONObject("lmsg").getJSONObject("p");
            String position = "x-" + pos.getString("x") + ", y-" + pos.getString("y");
            long time = System.currentTimeMillis() / 1000L - pos.getLong("t");
            String voltage = p.getString("pwr_ext");
            String name = obj.getString("nm");
            int speed = pos.getInt("s");

            double wheel1 = p.getInt("can32bitr6");
            double wheel2 = p.getInt("can32bitr7");
            double wheel3 = p.getInt("can32bitr12");
            double wheel4 = p.getInt("can32bitr13");
            double wheel5 = p.getInt("user_d4");
            double wheel6 = p.getInt("user_d5");
            double wheel7 = p.getInt("user_d6");
            double wheel8 = p.getInt("user_d7");

            StringBuffer wheels = new StringBuffer();
            if (wheel1 > 0 && wheel1 < 200) {
                wheels.append(wheel1 / 10 + ", ");
            }
            if (wheel2 > 0 && wheel2 < 200) {
                wheels.append(wheel2 / 10 + ", ");
            }
            if (wheel3 > 0 && wheel3 < 200) {
                wheels.append(wheel3 / 10 + ", ");
            }
            if (wheel4 > 0 && wheel4 < 200) {
                wheels.append(wheel4 / 10 + ", ");
            }
            if (wheel5 > 0 && wheel5 < 200) {
                wheels.append(wheel5 / 10 + ", ");
            }
            if (wheel6 > 0 && wheel6 < 200) {
                wheels.append(wheel6 / 10 + ", ");
            }
            if (wheel7 > 0 && wheel7 < 200) {
                wheels.append(wheel7 / 10 + ", ");
            }
            if (wheel8 > 0 && wheel8 < 200) {
                wheels.append(wheel8 / 10 + ", ");
            }
            wheels.replace(wheels.length() - 2, wheels.length() - 1, ".");

            int tormoz = p.getInt("can_r1");
            String tormozState = "нажат";
            if (tormoz == 220) {
                tormozState = "нажат";
            } else {
                tormozState = "отпущен";
            }

            int dopOs = p.getInt("can_r2");
            String dopOsState = "отпущена";
            if (dopOs == 252) {
                dopOsState = "отпущена";
            } else {
                dopOsState = "поднята";
            }

            int nagrOs1 = p.getInt("user_d0");
            int nagrOs2 = p.getInt("user_d1");
            int nagrOs3 = p.getInt("user_d2");
            int nagrOs4 = p.getInt("user_d3");
            int sumNagr = nagrOs1 +nagrOs2 + nagrOs3 + nagrOs4;

            double pressuareSystem = p.getDouble("can_r6")/20;


            StringBuffer nagrOs = new StringBuffer();
            nagrOs.append(nagrOs1 + ", ");
            nagrOs.append(nagrOs2 + ", ");
            nagrOs.append(nagrOs3 + ", ");
            if (wheel1 > 0 && wheel2 > 0 && wheel3 > 0 && wheel4 > 0 && wheel5 > 0 && wheel6 > 0 && wheel7 > 0 && wheel8 > 0) {
                nagrOs.append(nagrOs4 + ", ");
            }

            result.append("*Отчет о текущем состоянии ППЦ.*");
            result.append("\n");
            result.append("Объект: " + name);
            result.append("\n");
            result.append("Координаты на карте: " + position);
            result.append("\n");
            result.append("Скорость: " + speed + " км/ч");
            result.append("\n");
            result.append("[Показать на карте](https://yandex.ru/maps/?ll=" + pos.getString("x") + "%2C" + pos.getString("y") + "&mode=whatshere&whatshere%5B" +
                    "point%5D=" + pos.getString("x") + "%2C" + pos.getString("y") + "&whatshere%5Bzoom%5D=13.6&z=10)");
            result.append("\n");
            result.append("Последнее получение данных: " + convertSecToTime(time) + " назад");
            result.append("\n");
            result.append("Напряжение: " + voltage + " вольт");
            result.append("\n");
            result.append("Давление в колесах, бар: " + wheels);
            result.append("\n");
            result.append("Нагрузка на осях, т: " + nagrOs);
            result.append("\n");
            result.append("Суммарная нагрузка на оси, т: " + sumNagr);
            result.append("\n");
            result.append("Давление в пневмосистеме " + pressuareSystem + " бар");
            result.append("\n");
            result.append("Состояние тормоза: " + tormozState);
            result.append("\n");
            result.append("Дополнительная ось: " + dopOsState);

        } catch (Exception e) {
            e.printStackTrace();
            result.append("Произошла ошибка, попробуйте снова.");
        }
        return result;
    }

    private String convertSecToTime(long seconds) {
        long d = seconds / (3600 * 24);
        long h = (seconds % (3600 * 24)) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        String dd = (d > 0 ? d + "д " : "");
        String sh = (h > 0 ? h + "ч" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : m + "мин") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + s + "сек");
        return dd + sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }

}
