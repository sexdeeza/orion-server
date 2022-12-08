package handling.session;

import client.MapleClient;
import handling.PacketProcessor;
import handling.RecvPacketOpcode;
import handling.cashshop.CashShopOperationHandlers;
import handling.channel.handler.InterServerHandler;
import handling.channel.handler.PlayerHandler;
import lombok.extern.slf4j.Slf4j;
import server.config.ServerEnvironment;
import tools.FileOutputUtil;
import tools.HexTool;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;

@Slf4j
public class HandlerHelper {

    public static void handlePacket(MapleClient client, PacketProcessor processor, boolean isCashShop, byte[] message) {
        try {
            var slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(message));
            if (slea.available() < 2) {
                return;
            }
            var header_num = slea.readShort();
            var packetHandler = processor.getHandler(header_num);
            if (ServerEnvironment.isDebugEnabled()) {
                log.info("Received: " + header_num);
            }
            if (ServerEnvironment.isDebugEnabled() && packetHandler != null) {
                log.info("[" + packetHandler.getClass().getSimpleName() + "]");
            }
            if (packetHandler != null && packetHandler.validateState(client)) {
                packetHandler.handlePacket(slea, client);
                return;
            }
            for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
                if (recv.getValue() == header_num) {

                    if (!client.isReceiving()) {
                        return;
                    }
                    if (recv.needsChecking()) {
                        if (!client.isLoggedIn()) {
                            return;
                        }
                    }
                    HandlerHelper.handlePacket(recv, slea, client, isCashShop);
                    return;
                }
            }
            log.info("Received data: " + HexTool.toString(message));
            log.info("Data: " + new String(message));

        } catch (Exception e) {
            FileOutputUtil.outputFileError(FileOutputUtil.PacketEx_Log, e);
        }
    }


    public static void handlePacket(final RecvPacketOpcode header, final SeekableLittleEndianAccessor slea,
                                    final MapleClient c, boolean isCashShop) {
        switch (header) {
            case PLAYER_LOGGEDIN:
                final int playerId = slea.readInt();
                if (isCashShop) {
                    CashShopOperationHandlers.enterCashShop(playerId, c);
                } else {
                    InterServerHandler.loggedIn(playerId, c);
                }
                break;
            case CHANGE_MAP:
                if (isCashShop) {
                    CashShopOperationHandlers.leaveCashShop(slea, c, c.getPlayer());
                } else {
                    PlayerHandler.changeMap(slea, c, c.getPlayer());
                }
                break;
            default:
                if (slea.available() >= 0) {
                    FileOutputUtil.logPacket(String.valueOf(header), "[" + header + "] " + slea);
                }
                break;
        }
    }
}
