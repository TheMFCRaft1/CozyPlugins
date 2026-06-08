package dev.cozy.core;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;

/**
 * Checks for plugin updates via the Modrinth API.
 * <p>
 * Performs an asynchronous HTTP GET request to retrieve the latest version
 * and compares it against the current plugin version.
 */
public final class UpdateChecker {

    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/%s/version";

    private final JavaPlugin plugin;
    private final String modrinthId;

    /**
     * Creates a new UpdateChecker for the specified plugin and Modrinth project.
     *
     * @param plugin      the owning plugin
     * @param modrinthId  the Modrinth project ID or slug
     */
    public UpdateChecker(JavaPlugin plugin, String modrinthId) {
        this.plugin = plugin;
        this.modrinthId = modrinthId;
    }

    /**
     * Starts the asynchronous update check.
     * Logs whether an update is available or the plugin is up to date.
     */
    public void check() {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String url = String.format(MODRINTH_API_URL, modrinthId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseAndNotify)
                .exceptionally(ex -> {
                    plugin.getLogger().log(Level.WARNING, "Update check failed: {0}", ex.getMessage());
                    return null;
                });
    }

    /**
     * Parses the Modrinth API response and logs whether an update is available.
     * <p>
     * Expected JSON format (simplified):
     * <pre>
     * [
     *   {
     *     "version_number": "1.2.0",
     *     ...
     *   }
     * ]
     * </pre>
     *
     * @param jsonResponse the raw JSON string from the API
     */
    private void parseAndNotify(String jsonResponse) {
        String latestVersion = extractVersionNumber(jsonResponse);

        if (latestVersion == null) {
            plugin.getLogger().info("Could not parse latest version from Modrinth.");
            return;
        }

        String currentVersion = plugin.getDescription().getVersion();

        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            plugin.getLogger().log(Level.INFO, "A new version is available: {0} (current: {1})", new Object[]{latestVersion, currentVersion});
            plugin.getLogger().info("Download it at: https://modrinth.com/project/" + modrinthId);
        } else {
            plugin.getLogger().info("You are running the latest version.");
        }
    }

    /**
     * Extracts the "version_number" value from the JSON response using simple string parsing.
     *
     * @param json the raw JSON array string
     * @return the version number, or null if not found
     */
    private String extractVersionNumber(String json) {
        String key = "\"version_number\"";
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            return null;
        }

        int firstQuote = json.indexOf('"', colonIndex + 1);
        if (firstQuote == -1) {
            return null;
        }

        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote == -1) {
            return null;
        }

        return json.substring(firstQuote + 1, secondQuote);
    }
}
