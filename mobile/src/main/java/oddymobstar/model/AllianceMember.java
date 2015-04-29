package oddymobstar.model;

import com.google.android.gms.maps.model.LatLng;

import oddymobstar.message.in.InAllianceMessage;

/**
 * Created by timmytime on 28/04/15.
 */
public class AllianceMember {

    private String key;
    private String name;
    private String utm;
    private String subUtm;
    private double latitude;
    private double longitude;

    private Alliance alliance;

    public AllianceMember(Alliance alliance, InAllianceMessage allianceMessage) {

        this.alliance = alliance;

        setKey(allianceMessage.getAmid());
        setUtm(allianceMessage.getUtm());
        setLatitude(allianceMessage.getLatitude());
        setLongitude(allianceMessage.getLongitude());
        setSubUtm(allianceMessage.getSubUtm());

    }


    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUtm(String utm) {
        this.utm = utm;
    }

    public void setSubUtm(String subUtm) {
        this.subUtm = subUtm;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUtm() {
        return utm;
    }

    public String getSubUtm() {
        return subUtm;
    }
}
