package com.lorem_ipsum.models;

import com.lorem_ipsum.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by originally.us on 5/10/14.
 */
public class User implements Serializable {

    /*
    "id": 13,
    "email": "i@weikiat.net",
    "last_login_timestamp": 1420267814,
    "created_timestamp": 0,
    "secret": null,
    "secret_expiry_timestamp": 0,
    "reset_password": null,
    "verified_account": 0,
    "device_token": null,
    "banned": null,
    "modified_timestamp": 0,
    "display_name": "User"
    */

    public int id;
    public String email;
    public long last_login_timestamp;
    public long created_timestamp;
    public String secret;
    public long secret_expiry_timestamp;
    public int reset_password;
    public int verified_account;
    public String device_token;
    public int banned;
    public long modified_timestamp;
    public String display_name;

    public String getDisplayName() {
        if (StringUtils.isNotNull(display_name)) {
            return display_name;
        }
        return email;
    }

    public boolean isVerified() {
        if (StringUtils.isNull(secret)) {
            return false;
        }
        return true;
    }

}
