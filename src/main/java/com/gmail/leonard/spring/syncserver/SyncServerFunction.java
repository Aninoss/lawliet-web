package com.gmail.leonard.spring.syncserver;

import org.json.JSONObject;

import java.util.function.Function;

public interface SyncServerFunction extends Function<JSONObject, JSONObject> {}