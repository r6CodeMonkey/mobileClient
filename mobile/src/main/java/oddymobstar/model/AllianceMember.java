package oddymobstar.model;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import oddymobstar.database.DBHelper;
import oddymobstar.message.in.GridMessage;
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
    private double speed;
    private double altitude;

    private Alliance alliance;

    public AllianceMember(Alliance alliance, InAllianceMessage allianceMessage) {

        this.alliance = alliance;

        setKey(allianceMessage.getAmid());
        setUtm(allianceMessage.getUtm());
        setLatitude(allianceMessage.getLatitude());
        setLongitude(allianceMessage.getLongitude());
        setSubUtm(allianceMessage.getSubUtm());

    }

    public AllianceMember(GridMessage gridMessage) {


        setKey(gridMessage.getKey());
        setUtm(gridMessage.getUtm());
        setLatitude(gridMessage.getLatitude());
        setLongitude(gridMessage.getLongitude());
        setSubUtm(gridMessage.getSubUtm());
        setSpeed(gridMessage.getSpeed());
        setAltitude(gridMessage.getAltitude());

    }

    public AllianceMember(Cursor cursor) {
        setKey(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PLAYER_KEY)));
        setUtm(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.UTM)));
        setSubUtm(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.SUBUTM)));
        setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.LATITUDE)));
        setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.LONGITUDE)));
        setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PLAYER_NAME)));
        setSpeed(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.SPEED)));
        setAltitude(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.ALTITUDE)));
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
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

    public double getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }
}
