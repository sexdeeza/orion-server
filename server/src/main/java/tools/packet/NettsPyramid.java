package tools.packet;

import networking.data.output.OutPacket;
import networking.packet.SendPacketOpcode;

public final class NettsPyramid {

    private NettsPyramid() {}

    /**
     * Type values: 0 - cool 1 - kill 2 - miss 3 - buffs ps: expedia was here, don't remove my ign
     * pl0x
     */
    public static byte[] updatePyramidInfo(NettsInfoType type, int amount) {
        OutPacket packet = new OutPacket();
        packet.writeShort(SendPacketOpcode.ENERGY.getValue());
        packet.writeMapleAsciiString(getTypeValue(type));
        packet.writeMapleAsciiString(
                Integer.toString(amount)); // Just like dojo, nexon sends it like this (30 + amount)
        return packet.getPacket();
    }

    private static String getTypeValue(NettsInfoType type) {
        return "massacre_" + (type == NettsInfoType.COOL ? "cool" : type == NettsInfoType.KILL ? "hit" : "miss");
    }

    public enum NettsInfoType {
        COOL,
        KILL,
        MISS,
        BUFFS
    }
}
