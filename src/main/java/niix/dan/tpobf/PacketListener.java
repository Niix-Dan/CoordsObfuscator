package niix.dan.tpobf;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketListener extends PacketAdapter {

    public PacketListener(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.WORLD_EVENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            handleEntityTeleport(event);
        } else if (event.getPacketType() == PacketType.Play.Server.WORLD_EVENT) {
            handleWorldEvent(event);
        }
    }

    private void handleEntityTeleport(PacketEvent event) {
        int entityId = event.getPacket().getIntegers().read(0);
        Player teleportingPlayer = null;

        for (Player player : event.getPlayer().getWorld().getPlayers()) {
            if (player.getEntityId() == entityId) {
                teleportingPlayer = player;
                break;
            }
        }

        if (teleportingPlayer != null) {
            Location from = teleportingPlayer.getLocation();
            Location to = new Location(
                    from.getWorld(),
                    event.getPacket().getDoubles().read(0),
                    event.getPacket().getDoubles().read(1),
                    event.getPacket().getDoubles().read(2)
            );

            if (from.distance(to) > 100) {
                if (event.getPlayer().getEntityId() != teleportingPlayer.getEntityId()) {
                    // Obfuscate coordinates for other players
                    event.getPacket().getDoubles().write(0, 0.0);
                    event.getPacket().getDoubles().write(1, 0.0); // Ground level
                    event.getPacket().getDoubles().write(2, 0.0);
                }
            }
        }
    }

    private void handleWorldEvent(PacketEvent event) {
        int eventId = event.getPacket().getIntegers().read(0);
        if (eventId == 1023 || eventId == 1038 || eventId == 1028) {
            event.getPacket().getBlockPositionModifier().write(0, new BlockPosition(0, 0, 0));
        }
    }
}