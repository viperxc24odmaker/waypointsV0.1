package com.makeforge.waypoints;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class WaypointsClient implements ClientModInitializer {
    public static final String MOD_ID = "makeforgewaypoints";

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    @Override
    public void onInitializeClient() {
        KeyMapping addKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.makeforgewaypoints.add", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));
        KeyMapping removeKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.makeforgewaypoints.remove", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY));
        KeyMapping toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.makeforgewaypoints.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        // Render the waypoint overlay on top of the vanilla HUD.
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(MOD_ID, "waypoints"),
                WaypointHud::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            if (player == null) {
                return;
            }

            while (addKey.consumeClick()) {
                Waypoint wp = WaypointManager.get().addAtPlayer(player, null);
                player.displayClientMessage(Component.literal(
                        "\u00a7b[Waypoints] \u00a7fAdded \u00a7a" + wp.name
                                + " \u00a77(" + wp.x + ", " + wp.y + ", " + wp.z + ")"), false);
            }

            while (removeKey.consumeClick()) {
                Waypoint wp = WaypointManager.get().removeNearest(player);
                if (wp != null) {
                    player.displayClientMessage(Component.literal(
                            "\u00a7b[Waypoints] \u00a7fRemoved \u00a7c" + wp.name), false);
                } else {
                    player.displayClientMessage(Component.literal(
                            "\u00a7b[Waypoints] \u00a77No waypoints in this dimension."), false);
                }
            }

            while (toggleKey.consumeClick()) {
                WaypointHud.enabled = !WaypointHud.enabled;
                player.displayClientMessage(Component.literal(
                        "\u00a7b[Waypoints] \u00a7fHUD "
                                + (WaypointHud.enabled ? "\u00a7aON" : "\u00a7cOFF")), true);
            }
        });
    }
}
