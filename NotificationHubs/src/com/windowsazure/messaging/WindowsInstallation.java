//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a WNS Installation.
 */
public class WindowsInstallation extends Installation {

    private Map<String, WnsSecondaryTile> secondaryTiles;

    /**
     * Creates a new instance of the WindowsInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public WindowsInstallation(String installationId) {
        super(installationId, NotificationPlatform.Wns, null, (String[]) null);
    }

    /**
     * Creates a new instance of the WindowsInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public WindowsInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Wns, null, tags);
    }

    /**
     * Creates a new instance of the WindowsInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param pushChannel The push channel for the installation.
     */
    public WindowsInstallation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.Wns, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the Installation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public WindowsInstallation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Wns, pushChannel, tags);
    }

    /**
     * Gets the secondary tiles for WNS
     *
     * @return The secondary tiles for WNS.
     */
    public Map<String, WnsSecondaryTile> getSecondaryTiles() {
        return secondaryTiles;
    }

    /**
     * Adds a secondary tile to the installation template.
     *
     * @param tileName The name for the tile.
     * @param tile     THe WNS secondary tile.
     */
    public void addSecondaryTile(String tileName, WnsSecondaryTile tile) {
        if (secondaryTiles == null) {
            secondaryTiles = new HashMap<>();
        }

        secondaryTiles.put(tileName, tile);
    }

    /**
     * Removes the secondary WNS secondary tile by name.
     * @param tileName The name of the WNS secondary tile.
     */
    public void removeSecondaryTile(String tileName) {
        if (secondaryTiles == null) {
            return;
        }

        secondaryTiles.remove(tileName);
    }

    /**
     * Clears the WNS secondary tiles.
     */
    public void clearSecondaryTiles() {
        if (secondaryTiles == null) {
            return;
        }

        secondaryTiles.clear();
    }
}
