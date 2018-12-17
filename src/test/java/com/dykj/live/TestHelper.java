package com.dykj.live;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onvif.unofficial.OnvifDevice;
import org.onvif.unofficial.services.PtzService;
import org.onvif.ver10.schema.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestHelper {


    InputStream propStream;
    private OnvifDevice device;

    public static void main(String[] args) throws Exception {
        String address = "192.168.101.224";// props.getProperty("address");
        String login = "admin";// props.getProperty("login");
        String password = "admin123";//props.getProperty("password");
        System.out.println(address + "/" + login + "/" + password);
        OnvifDevice device = new OnvifDevice(address, login, password);
        List<Profile> profs = device.getMediaService().getProfiles();
        for (Profile profile : profs) {
            System.out.println("profile name: " + profile.getName() + "-----------------------");
            String token = profile.getToken();
            try {

                PtzService ptzService = device.getPtzService();
                //ptzService.absoluteMove(token,1.0f,1.0f,0.0f);
                //ptzService.stopMove(token);
                //ptzService.continuousMove(token,1.0f,1.0f,0.0f);
                ptzService.relativeMove(token, 1.0f, 1.0f, 0.0f);
            } catch (Exception e) {


            }
        }
    }

    @Before
    public void init() throws Exception {
        Properties props = new Properties();
        try {
            // you should create your own props file for test
//			 propStream = new FileInputStream("test.hkv2.properties");
//			propStream = new FileInputStream("test.wcam2.properties");
            //propStream = new FileInputStream("test.wcam.properties");
            //props.load(propStream);
            String address = "192.168.101.224";// props.getProperty("address");
            String login = "admin";// props.getProperty("login");
            String password = "admin123";//props.getProperty("password");
            System.out.println(address + "/" + login + "/" + password);
            device = new OnvifDevice(address, login, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCamData() {
        if (device == null)
            fail();
        Object val;
        try {
            val = device.getDeviceManagementService().getDeviceInformation().getSerialNumber();
            System.out.println(val);
            val = device.getDeviceManagementService().getHostname();
            System.out.println("hostname:" + val);
            List<Profile> profs = device.getMediaService().getProfiles();
            for (Profile profile : profs) {
                System.out.println("profile name: " + profile.getName() + "-----------------------");
                String token = profile.getToken();
                try {
                    val = device.getMediaService().getSnapshotUri(token);
                    System.out.println("snapshot: " + val);
                    PtzService ptzService = device.getPtzService();
                    //ptzService.absoluteMove(token,1.0f,1.0f,0.0f);
                    //ptzService.stopMove(token);
                    //ptzService.continuousMove(token,1.0f,1.0f,0.0f);
                    ptzService.relativeMove(token, 1.0f, 1.0f, 0.0f);
                } catch (Exception e) {

                }
                try {
                    val = device.getMediaService().getRTSPStreamUri(token);
                    System.out.println("rtsp stream: " + val);
                } catch (Exception e) {

                }
                try {
                    val = device.getMediaService().getHTTPStreamUri(token);
                    System.out.println("http stream: " + val);
                } catch (Exception e) {

                }
                try {
                    VideoEncoderConfiguration conf = profile.getVideoEncoderConfiguration();
                    System.out.println("encodining: " + conf.getEncoding());
                    System.out.println(
                            "size: " + conf.getResolution().getWidth() + "x" + conf.getResolution().getHeight());
                    System.out.println("FPS:" + conf.getRateControl().getFrameRateLimit());
                    System.out.println("GOP:" + conf.getRateControl().getEncodingInterval());
                    System.out.println("bitrate:" + conf.getRateControl().getBitrateLimit());
                    System.out.println("quality:" + conf.getQuality());

                } catch (Exception e) {

                }
                try {
                    VideoEncoderConfigurationOptions options = device.getMediaService()
                            .getVideoEncoderConfigurationOptions(token);
                    List<VideoResolution> ress = options.getH264().getResolutionsAvailable();
                    for (VideoResolution videoResolution : ress) {
                        System.out.println("	resolution supported:" + videoResolution.getWidth() + "x"
                                + videoResolution.getHeight());
                    }
                } catch (Exception e) {
                    System.err.println("error getting VideoEncoderConfigurationOptions");
                }
            }
            assertNotNull("test");
        } catch (Exception e) {
            // e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testVideosources() {
        Object val;
        try {
            List<VideoSource> sources = device.getMediaService().getVideoSources();
            for (VideoSource videoSource : sources) {
                String token = videoSource.getToken();
                System.out.println("videoSource: " + token + " -----------------");
                System.out.println("framerate: " + videoSource.getFramerate());
                System.out.println("size: " + videoSource.getResolution().getWidth() + "x"
                        + videoSource.getResolution().getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testVideosourceConfigs() {
        Object val;
        try {
            List<VideoSourceConfiguration> configs = device.getMediaService().getVideoSourceConfigurations();
            for (VideoSourceConfiguration config : configs) {
                String token = config.getToken();
                System.out.println("vidConfig: " + token + " -----------------");
                System.out.println("name: " + config.getName());
                System.out.println("size: " + config.getBounds().getWidth() + "x" + config.getBounds().getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testNetwork() {
        Object val;
        try {
            List<NetworkInterface> ifaces = device.getDeviceManagementService().getNetworkInterfaces();
            for (NetworkInterface networkInterface : ifaces) {
                System.out.println("interface name: " + networkInterface.getInfo().getName());
                try {
                    val = networkInterface.getInfo().getHwAddress();
                    System.out.println(val);

                } catch (Exception e) {
                    System.out.println("Error getting IP address");
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting NetworkInterfaces");
        }
    }

    @After
    public void close() {
        if (propStream != null) {
            try {
                propStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
