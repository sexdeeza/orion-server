package networking.packet;

import client.MapleClient;
import networking.data.input.InPacket;

public interface MaplePacketHandler {

    void handlePacket(final InPacket packet, final MapleClient c);

    boolean validateState(MapleClient c);
}
