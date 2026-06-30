import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.landinfo.client.ForestTreeNetClient;
import com.landinfo.client.LoginException;

import java.util.Arrays;

/**
 * BasicUsage — a complete end-to-end walk through the five public methods of
 * {@link ForestTreeNetClient}.
 *
 * <p>Workflow:
 * <ol>
 *   <li>{@code login()} to obtain a Bearer token (valid 24 h).</li>
 *   <li>{@code queryTreeLandRanges()} — list sample plots (样地).</li>
 *   <li>{@code queryTreeInfos()} — list sample trees (样木) in the first plot.</li>
 *   <li>{@code queryTreeMeasureStations()} — list the tree-diameter devices bound to those trees.</li>
 *   <li>{@code queryTreeMeasureData()} — pull the latest 5 readings from the first device.</li>
 * </ol>
 *
 * <p><b>Before running:</b> set your credentials and server URL. The simplest
 * way is the JVM property:
 *
 * <pre>{@code
 * java -DforestTreeNet.serverUrl=https://your-host/forest-tree-net/ \
 *      -cp "lib/forest-tree-net-client-1.0.0.jar:." BasicUsage
 * }</pre>
 *
 * <p>Or edit {@code USERNAME}, {@code PASSWORD}, {@code SERVER_URL} below.
 *
 * @author  BtTreeGauge contributors
 * @license MIT
 */
public class BasicUsage {

    /** Set these before running, or pass them via -D properties / env vars. */
    private static final String USERNAME  = System.getProperty("ftn.username",  "demo-user");
    private static final String PASSWORD  = System.getProperty("ftn.password",  "demo-pass");

    public static void main(String[] args) throws LoginException {
        // ── 1. Login ─────────────────────────────────────────────────────────
        JSONObject loginResp = ForestTreeNetClient.login(USERNAME, PASSWORD);
        if (!loginResp.getBooleanValue("success")) {
            System.err.println("login failed: " + loginResp.getString("msg"));
            System.exit(1);
        }
        String token = loginResp.getString("token");
        System.out.println("[1] logged in, token = " + abbreviate(token));

        // ── 2. Sample plots ──────────────────────────────────────────────────
        JSONObject plotsBody = new JSONObject();
        plotsBody.put("page", 0);
        plotsBody.put("size", 5);
        plotsBody.put("isDelete", false);
        plotsBody.put("sortList", Arrays.asList("create_date_time desc"));

        JSONObject plotsResp = ForestTreeNetClient.queryTreeLandRanges(token, plotsBody);
        if (!plotsResp.getBooleanValue("success")) {
            System.err.println("queryTreeLandRanges failed: " + plotsResp.getString("msg"));
            System.exit(1);
        }
        JSONArray plots = plotsResp.getJSONArray("content");
        System.out.printf("[2] %d / %d plots%n", plots.size(), plotsResp.getIntValue("totalElements"));
        if (plots.isEmpty()) {
            System.out.println("no plots — nothing more to do.");
            return;
        }

        // Pick the first plot
        JSONObject firstPlot = plots.getJSONObject(0);
        String landNumber = firstPlot.getString("landNumber");
        System.out.println("    → first plot: " + landNumber + " (" + firstPlot.getString("name") + ")");

        // ── 3. Sample trees ──────────────────────────────────────────────────
        JSONObject treesBody = new JSONObject();
        treesBody.put("landNumber", landNumber);
        treesBody.put("page", 0);
        treesBody.put("size", 5);

        JSONObject treesResp = ForestTreeNetClient.queryTreeInfos(token, treesBody);
        if (!treesResp.getBooleanValue("success")) {
            System.err.println("queryTreeInfos failed: " + treesResp.getString("msg"));
            System.exit(1);
        }
        JSONArray trees = treesResp.getJSONArray("content");
        System.out.printf("[3] %d / %d trees in plot %s%n",
                trees.size(), treesResp.getIntValue("totalElements"), landNumber);

        if (trees.isEmpty()) {
            System.out.println("no trees — skipping device & data queries.");
            return;
        }

        // ── 4. Tree-diameter devices ─────────────────────────────────────────
        JSONObject stationsBody = new JSONObject();
        stationsBody.put("landNumber", landNumber);
        stationsBody.put("returnTree", false);
        stationsBody.put("returnData", true);
        stationsBody.put("page", 0);
        stationsBody.put("size", 5);

        JSONObject stationsResp = ForestTreeNetClient.queryTreeMeasureStations(token, stationsBody);
        if (!stationsResp.getBooleanValue("success")) {
            System.err.println("queryTreeMeasureStations failed: " + stationsResp.getString("msg"));
            System.exit(1);
        }
        JSONArray stations = stationsResp.getJSONArray("data");
        System.out.printf("[4] %d / %d devices in plot %s%n",
                stations.size(), stationsResp.getIntValue("totalElements"), landNumber);

        if (stations.isEmpty()) {
            System.out.println("no devices — skipping history query.");
            return;
        }

        // ── 5. Historical readings for the first device ──────────────────────
        JSONObject firstDevice = stations.getJSONObject(0);
        String sn = firstDevice.getString("sn");
        System.out.println("    → first device: " + sn
                + " (DBH=" + firstDevice.getDoubleValue("dm") + " mm)");

        JSONObject dataBody = new JSONObject();
        dataBody.put("sn", sn);
        dataBody.put("page", 0);
        dataBody.put("size", 5);

        JSONObject dataResp = ForestTreeNetClient.queryTreeMeasureData(token, dataBody);
        if (!dataResp.getBooleanValue("success")) {
            System.err.println("queryTreeMeasureData failed: " + dataResp.getString("msg"));
            System.exit(1);
        }
        JSONArray readings = dataResp.getJSONArray("data");
        System.out.printf("[5] %d / %d readings for device %s%n",
                readings.size(), dataResp.getIntValue("totalElements"), sn);

        for (int i = 0; i < readings.size(); i++) {
            JSONObject r = readings.getJSONObject(i);
            System.out.printf("    - %s | dm=%.2fmm | angle=%.2f° | bat=%.0fmV%n",
                    r.getString("reportDateTime"),
                    r.getDoubleValue("dm"),
                    r.getDoubleValue("angle"),
                    r.getDoubleValue("bat"));
        }
    }

    /** Shorten a token for safe printing. */
    private static String abbreviate(String token) {
        if (token == null || token.length() < 12) return String.valueOf(token);
        return token.substring(0, 8) + "…" + token.substring(token.length() - 4);
    }
}