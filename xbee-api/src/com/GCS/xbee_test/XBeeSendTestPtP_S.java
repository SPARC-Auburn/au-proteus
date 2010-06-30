package com.GCS.xbee_test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeFrameIdResponse;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;

public class XBeeSendTestPtP_S {

	private static XBee xbee;
	private final static Logger log = Logger.getLogger(XBeeSendTestPtP_S.class);

	private static final int CONSTANT = 123;		// constant number to fill packet
	private static final int PKT_SIZE_INTS = 21;	// packet payload size of 84 bytes (32-bit ints)
	private static final int NUM_PACKETS = 250;		// number of packets to send
	private static final int DELAY = 0;			// milliseconds of delay between packet transmits
	//private static final XBeeAddress64 DEST_64 = new XBeeAddress64(0, 0x13, 0xA2, 0, 0x40, 0x62, 0xD6, 0xED);	// IEEE address of receiving node (API)
	private static final XBeeAddress64 DEST_64 = new XBeeAddress64(0, 0x13, 0xA2, 0, 0x40, 0x62, 0xD6, 0xEE);	// IEEE address of receiving node (AT)

	public static void main(String[] args) throws InterruptedException {
		PropertyConfigurator.configure("log4j.properties");
		xbee = new XBee();
		int[] payload = new int[PKT_SIZE_INTS*4];	// payload[] entries are actually bytes, but XBee-API doesn't like that primitive...

		int packetCount = 0;
		int errorCount = 0;
		long startTime = 0;

		// initialize little endian packet payload (payload[0-3] holds packet count)
		for (int i = 4; i < PKT_SIZE_INTS*4; i+=4) payload[i] = CONSTANT;

		try {
			xbee.open("/dev/ttyUSB0", 115200);
			xbee.addPacketListener(new ACKPacketListener());
			XBeeAddress16 dest_16 = Shared.get16Addr(xbee, DEST_64);

			while (packetCount < NUM_PACKETS) {
				// delay before sending next packet
				Thread.sleep(DELAY);
				// 1st packet is always slow, skip counting it
				if (packetCount == 1) startTime  = System.currentTimeMillis();

				// send packet and calculate latency after receiving ACK
				long beforeSend = System.nanoTime();
				try {
					//xbee.sendSynchronous(new ZNetTxRequest(1, DEST_64, dest_16, 0, 1, payload), 5000);
					//xbee.sendSynchronous(new ZNetTxRequest(DEST_64, payload), 5000);
					xbee.sendAsynchronous(new ZNetTxRequest(packetCount, DEST_64, dest_16, 0, 0, payload));
				} catch (XBeeTimeoutException e) {
					errorCount++;
					log.error("ERROR, ACK 5sec timeout");
				}
				long latency = System.nanoTime() - beforeSend;

				// output individual packet results
				log.debug("Packet " + packetCount + ": Latency of " + latency/1000000 + " ms.");
				log.info(packetCount+","+latency);

				//update packet count
				packetCount++;
				payload[3] = (packetCount >> 24) & 0xFF;
				payload[2] = (packetCount >> 16) & 0xFF;
				payload[1] = (packetCount >> 8) & 0xFF;
				payload[0] = packetCount & 0xFF;
			}

			long totalLatency = System.currentTimeMillis() - startTime;
			int totalPackets = NUM_PACKETS - errorCount - 1;	// -1 for not counting 1st packet

			log.info("Delay:\t\t"+DELAY+"ms");
			log.info("Errors:\t\t" + errorCount + " packets");
			log.info("RSSI:\t\t"+ Shared.getRSSI(xbee)+"dBm");
			log.info("Goodput:\t" + ((float)(totalPackets*PKT_SIZE_INTS*4*8)/(float)totalLatency) +"kbps");
			log.info("Throughput:\t" + ((float)(totalPackets*128*8)/(float)totalLatency) + "kbps");
		}
		catch (XBeeException e) {
			e.printStackTrace();
		}
		finally {
			xbee.close();
		}

	}
	
	private static class ACKPacketListener implements PacketListener {

		@Override
		public void processResponse(XBeeResponse response) {
			if (response instanceof XBeeFrameIdResponse /*&& ((XBeeFrameIdResponse)response).getFrameId() == xbeeRequest.getFrameId()*/) {
				log.info("ACK: frame ID of " + ((XBeeFrameIdResponse)response).getFrameId());
			}
			
		}
		
	}
}
