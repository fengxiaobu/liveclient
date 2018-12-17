package org.onvif.unofficial.services;

import org.onvif.unofficial.OnvifDevice;
import org.onvif.unofficial.soapclient.ISoapClient;
import org.onvif.ver10.device.wsdl.*;
import org.onvif.ver10.schema.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DeviceManagementService extends AbstractService {

    public DeviceManagementService(OnvifDevice onvifDevice, ISoapClient client, String serviceUrl) {
        super(onvifDevice, client, serviceUrl);
    }

    public java.util.Date getDate() throws Exception {
        GetSystemDateAndTimeResponse response = client.processRequest(new GetSystemDateAndTime(),
                GetSystemDateAndTimeResponse.class, serviceUrl, true);
        Date date = response.getSystemDateAndTime().getUTCDateTime().getDate();
        Time time = response.getSystemDateAndTime().getUTCDateTime().getTime();
        Calendar cal = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(), time.getHour(),
                time.getMinute(), time.getSecond());
        return cal.getTime();
    }

    public void setSystemDateAndTime(Calendar calendar) throws Exception {
        SetSystemDateAndTime request = new SetSystemDateAndTime();
        request.setDateTimeType(SetDateTimeType.MANUAL);
        request.setDaylightSavings(calendar.getTimeZone().useDaylightTime());
        TimeZone tz = new TimeZone();
        tz.setTZ(calendar.getTimeZone().getID());
        request.setTimeZone(tz);
        DateTime dt = new DateTime();
        Date d = new Date();
        d.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        d.setMonth(calendar.get(Calendar.MONTH));
        d.setYear(calendar.get(Calendar.YEAR));
        dt.setDate(d);
        Time t = new Time();
        t.setHour(calendar.get(Calendar.HOUR));
        t.setMinute(calendar.get(Calendar.MINUTE));
        t.setSecond(calendar.get(Calendar.SECOND));
        dt.setTime(t);
        request.setUTCDateTime(dt);
        client.processRequest(request, SetSystemDateAndTimeResponse.class, serviceUrl, true);
    }

    public GetDeviceInformationResponse getDeviceInformation() throws Exception {
        return client.processRequest(new GetDeviceInformation(), GetDeviceInformationResponse.class, serviceUrl, true);
    }

    public String getHostname() throws Exception {
        GetHostnameResponse response = client.processRequest(new GetHostname(), GetHostnameResponse.class, serviceUrl,
                true);
        return response.getHostnameInformation().getName();
    }

    public void setHostname(String hostname) throws Exception {
        SetHostname request = new SetHostname();
        request.setName(hostname);
        client.processRequest(request, SetHostnameResponse.class, serviceUrl, true);
    }

    public List<User> getUsers() throws Exception {
        GetUsersResponse response = client.processRequest(new GetUsers(), GetUsersResponse.class, serviceUrl, true);
        return response.getUser();
    }

    public Capabilities getCapabilities() throws Exception {
        GetCapabilitiesResponse response = client.processRequest(new GetCapabilities(), GetCapabilitiesResponse.class,
                serviceUrl, false);
        return response.getCapabilities();
    }

    public List<Service> getServices(boolean includeCapability) throws Exception {
        GetServices request = new GetServices();
        request.setIncludeCapability(includeCapability);
        GetServicesResponse response = client.processRequest(request, GetServicesResponse.class, serviceUrl, true);
        if (response == null) {
            return null;
        }
        return response.getService();
    }

    public List<Scope> getScopes() throws Exception {
        GetScopesResponse response = client.processRequest(new GetScopes(), GetScopesResponse.class, serviceUrl, true);
        if (response == null) {
            return null;
        }
        return response.getScopes();
    }


    public String reboot() throws Exception {
        SystemRebootResponse response = client.processRequest(new SystemReboot(), SystemRebootResponse.class,
                serviceUrl, true);
        if (response == null) {
            return null;
        }
        return response.getMessage();
    }

    public List<NetworkInterface> getNetworkInterfaces() throws Exception {
        GetNetworkInterfacesResponse response = client.processRequest(new GetNetworkInterfaces(),
                GetNetworkInterfacesResponse.class, serviceUrl, true);
        if (response == null)
            return null;
        return response.getNetworkInterfaces();

    }

    public Dot11Capabilities getDot11Capabilities() throws Exception {
        GetDot11CapabilitiesResponse response = client.processRequest(new GetDot11Capabilities(),
                GetDot11CapabilitiesResponse.class, serviceUrl, true);
        if (response == null)
            return null;
        return response.getCapabilities();
    }

    public List<Dot1XConfiguration> getDot1XConfigurations() throws Exception {
        GetDot1XConfigurationsResponse response = client.processRequest(new GetDot1XConfigurations(),
                GetDot1XConfigurationsResponse.class, serviceUrl, true);
        if (response == null)
            return null;
        return response.getDot1XConfiguration();
    }
}
