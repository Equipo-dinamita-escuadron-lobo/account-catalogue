package com.account_catalogue.commons.security;

import java.util.List;

public interface IJwtUtils {

    String getId();

    String getToken();

    String getUsername();

    List<String> getRealmRoles();

}