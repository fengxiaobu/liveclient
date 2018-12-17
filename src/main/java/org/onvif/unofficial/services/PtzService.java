package org.onvif.unofficial.services;

import org.onvif.unofficial.OnvifDevice;
import org.onvif.unofficial.soapclient.ISoapClient;
import org.onvif.ver10.schema.*;
import org.onvif.ver20.ptz.wsdl.*;

import java.util.List;

public class PtzService extends AbstractService {

    public PtzService(OnvifDevice onvifDevice, ISoapClient client, String serviceUrl) {
        super(onvifDevice, client, serviceUrl);
    }

    public boolean isPtzOperationsSupported(String profileToken) throws Exception {
        return getPTZConfiguration(profileToken) != null;
    }

    /**
     * @param profileToken
     * @return If is null, PTZ operations are not supported
     * @throws Exception
     */
    public PTZConfiguration getPTZConfiguration(String profileToken) throws Exception {
        if (profileToken == null || profileToken.equals("")) {
            return null;
        }
        Profile profile = onvifDevice.getMediaService().getProfile(profileToken);
        if (profile == null) {
            throw new IllegalArgumentException("No profile available for token: " + profileToken);
        }
        if (profile.getPTZConfiguration() == null) {
            return null; // no PTZ support
        }

        return profile.getPTZConfiguration();
    }

    public List<PTZNode> getNodes() throws Exception {
        GetNodesResponse response = client.processRequest(new GetNodes(), GetNodesResponse.class, serviceUrl, true);
        if (response == null)
            return null;
        return response.getPTZNode();
    }

    public PTZNode getNode(String profileToken) throws Exception {
        return getNode(getPTZConfiguration(profileToken));
    }

    public PTZNode getNode(PTZConfiguration ptzConfiguration) throws Exception {
        GetNode request = new GetNode();
        if (ptzConfiguration == null) {
            return null; // no PTZ support
        }
        request.setNodeToken(ptzConfiguration.getNodeToken());
        GetNodeResponse response = client.processRequest(request, GetNodeResponse.class, serviceUrl, true);
        if (response == null)
            return null;
        return response.getPTZNode();
    }

    public FloatRange getPanSpaces(String profileToken) throws Exception {
        PTZNode node = getNode(profileToken);

        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ptzSpaces.getAbsolutePanTiltPositionSpace().get(0).getXRange();
    }

    public FloatRange getTiltSpaces(String profileToken) throws Exception {
        PTZNode node = getNode(profileToken);

        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ptzSpaces.getAbsolutePanTiltPositionSpace().get(0).getYRange();
    }

    public FloatRange getZoomSpaces(String profileToken) throws Exception {
        PTZNode node = getNode(profileToken);

        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ptzSpaces.getAbsoluteZoomPositionSpace().get(0).getXRange();
    }

    public boolean isAbsoluteMoveSupported(String profileToken) throws Exception {
        Profile profile = onvifDevice.getMediaService().getProfile(profileToken);
        return profile.getPTZConfiguration().getDefaultAbsolutePantTiltPositionSpace() != null;
    }

    /**
     * @param x    Pan-Position
     * @param y    Tilt-Position
     * @param zoom Zoom
     * @return True if move successful
     * @throws Exception
     * @see getPanSpaces(), getTiltSpaces(), getZoomSpaces()
     */
    public boolean absoluteMove(String profileToken, float x, float y, float zoom) throws Exception {
        PTZNode node = getNode(profileToken);
        if (node != null) {
            FloatRange xRange = node.getSupportedPTZSpaces().getAbsolutePanTiltPositionSpace().get(0).getXRange();
            FloatRange yRange = node.getSupportedPTZSpaces().getAbsolutePanTiltPositionSpace().get(0).getYRange();
            FloatRange zRange = node.getSupportedPTZSpaces().getAbsoluteZoomPositionSpace().get(0).getXRange();

            if (zoom < zRange.getMin() || zoom > zRange.getMax()) {
                throw new IllegalArgumentException("Bad value for zoom: " + zoom);
            }
            if (x < xRange.getMin() || x > xRange.getMax()) {
                throw new IllegalArgumentException("Bad value for pan:/x " + x);
            }
            if (y < yRange.getMin() || y > yRange.getMax()) {
                throw new IllegalArgumentException("Bad value for tilt/y: " + y);
            }
        }
        AbsoluteMove request = new AbsoluteMove();
        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);
        PTZVector ptzVector = new PTZVector();
        ptzVector.setPanTilt(panTiltVector);
        ptzVector.setZoom(zoomVector);
        request.setPosition(ptzVector);
        request.setProfileToken(profileToken);
        AbsoluteMoveResponse response = client.processRequest(request, AbsoluteMoveResponse.class, serviceUrl, true);
        return response != null;
    }

    public boolean isRelativeMoveSupported(String profileToken) throws Exception {
        if (onvifDevice.getMediaService() == null)
            return false;
        Profile profile = onvifDevice.getMediaService().getProfile(profileToken);
        PTZConfiguration ptzConf = profile.getPTZConfiguration();
        if (ptzConf == null)
            return false;
        return ptzConf.getDefaultRelativePanTiltTranslationSpace() != null;
    }

    public boolean relativeMove(String profileToken, float x, float y, float zoom) throws Exception {

        RelativeMove request = new RelativeMove();
        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);

        PTZVector translation = new PTZVector();
        translation.setPanTilt(panTiltVector);
        translation.setZoom(zoomVector);

        request.setProfileToken(profileToken);
        request.setTranslation(translation);
        RelativeMoveResponse response = client.processRequest(request, RelativeMoveResponse.class, serviceUrl, true);
        return response != null;
    }

    public boolean isContinuosMoveSupported(String profileToken) throws Exception {
        if (onvifDevice.getMediaService() == null)
            return false;
        Profile profile = onvifDevice.getMediaService().getProfile(profileToken);
        PTZConfiguration ptzConf = profile.getPTZConfiguration();
        if (ptzConf == null)
            return false;
        return ptzConf.getDefaultContinuousPanTiltVelocitySpace() != null;
    }

    public boolean continuousMove(String profileToken, float x, float y, float zoom) throws Exception {
        ContinuousMove request = new ContinuousMove();

        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);
        PTZSpeed ptzSpeed = new PTZSpeed();
        ptzSpeed.setPanTilt(panTiltVector);
        ptzSpeed.setZoom(zoomVector);
        request.setVelocity(ptzSpeed);
        request.setProfileToken(profileToken);
        ContinuousMoveResponse response = client.processRequest(request, ContinuousMoveResponse.class, serviceUrl,
                true);
        return response != null;
    }

    public boolean stopMove(String profileToken) throws Exception {
        Stop request = new Stop();
        request.setPanTilt(true);
        request.setZoom(true);
        request.setProfileToken(profileToken);
        StopResponse response = client.processRequest(request, StopResponse.class, serviceUrl, true);
        return response != null;
    }

    public PTZStatus getStatus(String profileToken) throws Exception {
        GetStatus request = new GetStatus();
        request.setProfileToken(profileToken);
        GetStatusResponse response = client.processRequest(request, GetStatusResponse.class, serviceUrl, true);
        if (response == null) {
            return null;
        }
        return response.getPTZStatus();
    }

    public PTZVector getPosition(String profileToken) throws Exception {
        PTZStatus status = getStatus(profileToken);
        if (status == null) {
            return null;
        }
        return status.getPosition();
    }

    public boolean setHomePosition(String profileToken) throws Exception {
        SetHomePosition request = new SetHomePosition();
        request.setProfileToken(profileToken);
        SetHomePositionResponse response = client.processRequest(request, SetHomePositionResponse.class, serviceUrl,
                true);
        return response != null;
    }

    public List<PTZPreset> getPresets(String profileToken) throws Exception {
        GetPresets request = new GetPresets();
        request.setProfileToken(profileToken);
        GetPresetsResponse response = client.processRequest(request, GetPresetsResponse.class, serviceUrl, true);
        if (response == null) {
            return null;
        }
        return response.getPreset();
    }

    public String setPreset(String presetName, String presetToken, String profileToken) throws Exception {
        SetPreset request = new SetPreset();
        request.setProfileToken(profileToken);
        request.setPresetName(presetName);
        request.setPresetToken(presetToken);
        SetPresetResponse response = client.processRequest(request, SetPresetResponse.class, serviceUrl, true);
        if (response == null) {
            return null;
        }
        return response.getPresetToken();
    }

    public String setPreset(String presetName, String profileToken) throws Exception {
        return setPreset(presetName, null, profileToken);
    }

    public boolean removePreset(String presetToken, String profileToken) throws Exception {
        RemovePreset request = new RemovePreset();
        request.setProfileToken(profileToken);
        request.setPresetToken(presetToken);
        RemovePresetResponse response = client.processRequest(request, RemovePresetResponse.class, serviceUrl, true);
        return response != null;
    }

    public boolean gotoPreset(String presetToken, String profileToken) throws Exception {
        GotoPreset request = new GotoPreset();
        request.setProfileToken(profileToken);
        request.setPresetToken(presetToken);
        GotoPresetResponse response = client.processRequest(request, GotoPresetResponse.class, serviceUrl, true);
        return response != null;
    }
}
