package com.dnhnd.godotappodeal;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.AppodealUnityBannerView;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.MrecCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.inapp.InAppPurchase;
import com.appodeal.ads.inapp.InAppPurchaseValidateCallback;
import com.appodeal.ads.regulator.CCPAUserConsent;
import com.appodeal.ads.regulator.GDPRUserConsent;
import com.appodeal.ads.service.ServiceError;
import com.appodeal.ads.utils.Log;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class AppodealPlugin extends GodotPlugin {

    private Activity godotActivity;

    public AppodealPlugin(Godot godot) { super(godot); }

    @NonNull
    @Override
    public String getPluginName() {
        return "Appodeal";
    }

    @Nullable
    @Override
    public View onMainCreate(Activity activity) {
        godotActivity = activity;
        return new FrameLayout(activity);
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo(AppodealSignals.ON_INITIALIZATION_FINISHED, String.class));

        signals.add(new SignalInfo(AppodealSignals.ON_AD_REVENUE_RECEIVE, Dictionary.class));

        signals.add(new SignalInfo(AppodealSignals.ON_INAPP_PURCHASE_VALIDATE_SUCCESS, String.class));
        signals.add(new SignalInfo(AppodealSignals.ON_INAPP_PURCHASE_VALIDATE_FAIL, String.class));

        signals.add(new SignalInfo(AppodealSignals.ON_MREC_LOADED, Boolean.class));
        signals.add(new SignalInfo(AppodealSignals.ON_MREC_FAILED_TO_LOAD));
        signals.add(new SignalInfo(AppodealSignals.ON_MREC_SHOWN));
        signals.add(new SignalInfo(AppodealSignals.ON_MREC_SHOW_FAILED));
        signals.add(new SignalInfo(AppodealSignals.ON_MREC_CLICKED));
        signals.add(new SignalInfo(AppodealSignals.ON_MREC_EXPIRED));

        signals.add(new SignalInfo(AppodealSignals.ON_BANNER_LOADED, Integer.class, Boolean.class));
        signals.add(new SignalInfo(AppodealSignals.ON_BANNER_FAILED_TO_LOAD));
        signals.add(new SignalInfo(AppodealSignals.ON_BANNER_SHOWN));
        signals.add(new SignalInfo(AppodealSignals.ON_BANNER_SHOW_FAILED));
        signals.add(new SignalInfo(AppodealSignals.ON_BANNER_CLICKED));
        signals.add(new SignalInfo(AppodealSignals.ON_BANNER_EXPIRED));

        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_LOADED, Boolean.class));
        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_FAILED_TO_LOAD));
        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_SHOWN));
        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_SHOW_FAILED));
        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_CLICKED));
        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_CLOSED));
        signals.add(new SignalInfo(AppodealSignals.ON_INTERSTITIAL_EXPIRED));

        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_LOADED, Boolean.class));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_FAILED_TO_LOAD));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_SHOWN));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_SHOW_FAILED));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_CLICKED));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_FINISHED, Float.class, String.class));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_CLOSED, Boolean.class));
        signals.add(new SignalInfo(AppodealSignals.ON_REWARDED_VIDEO_EXPIRED));

        return signals;
    }

    @UsedByGodot
    public void initialize(String appKey, int adTypes) {
        setCallbacks();

        Appodeal.initialize(godotActivity, appKey, getAndroidAdTypes(adTypes), list -> {
            if (list == null || list.isEmpty()) {
                emitSignal(AppodealSignals.ON_INITIALIZATION_FINISHED, "");
            }
            else {
                emitSignal(AppodealSignals.ON_INITIALIZATION_FINISHED, TextUtils.join(", ", list));
            }
        });
    }

    @UsedByGodot
    public boolean isInitialized(int adType) {
        return Appodeal.isInitialized(getAndroidAdType(adType));
    }

    @UsedByGodot
    public void updateGDPRUserConsent(int consent) {
        switch (consent) {
            case 0:
                Appodeal.updateGDPRUserConsent(GDPRUserConsent.Personalized);
                break;
            case 1:
                Appodeal.updateGDPRUserConsent(GDPRUserConsent.NonPersonalized);
                break;
            case 2:
            default:
                Appodeal.updateGDPRUserConsent(GDPRUserConsent.Unknown);
        }
    }

    @UsedByGodot
    public void updateCCPAUserConsent(int consent) {
        switch (consent) {
            case 0:
                Appodeal.updateCCPAUserConsent(CCPAUserConsent.OptIn);
                break;
            case 1:
                Appodeal.updateCCPAUserConsent(CCPAUserConsent.OptOut);
                break;
            case 2:
            default:
                Appodeal.updateCCPAUserConsent(CCPAUserConsent.Unknown);
        }
    }

    @UsedByGodot
    public boolean isAutoCacheEnabled(int adType) {
        return Appodeal.isAutoCacheEnabled(getAndroidAdType(adType));
    }

    @UsedByGodot
    public void cache(int adTypes) {
        Appodeal.cache(godotActivity, getAndroidAdTypes(adTypes));
    }

    @UsedByGodot
    public boolean show(int adType) {
        return Appodeal.show(godotActivity, getAndroidShowStyle(adType));
    }

    @UsedByGodot
    public boolean showForPlacement(int adType, String placement) {
        return Appodeal.show(godotActivity, getAndroidShowStyle(adType), placement);
    }

    @UsedByGodot
    public boolean showBannerView(int xAxis, int yAxis, String placement) {
        return AppodealUnityBannerView.getInstance().showBannerView(godotActivity, xAxis, getAndroidYAxisPos(yAxis), placement);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @UsedByGodot
    public boolean showMrecView(int xAxis, int yAxis, String placement) {
        return AppodealUnityBannerView.getInstance().showMrecView(godotActivity, xAxis, getAndroidYAxisPos(yAxis), placement);
    }

    @UsedByGodot
    public void hideBanner() {
        Appodeal.hide(godotActivity, Appodeal.BANNER);
    }

    @UsedByGodot
    public void hideBannerView() {
        AppodealUnityBannerView.getInstance().hideBannerView(godotActivity);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @UsedByGodot
    public void hideMrecView() {
        AppodealUnityBannerView.getInstance().hideMrecView(godotActivity);
    }

    @UsedByGodot
    public void setAutoCache(int adTypes, boolean autoCache) {
        Appodeal.setAutoCache(getAndroidAdTypes(adTypes), autoCache);
    }

    @UsedByGodot
    public boolean isLoaded(int adTypes) {
        return Appodeal.isLoaded(getAndroidAdTypes(adTypes));
    }

    @UsedByGodot
    public boolean isPrecache(int adType) {
        return Appodeal.isPrecache(getAndroidAdType(adType));
    }

    @UsedByGodot
    public void setSmartBanners(boolean enabled) {
        Appodeal.setSmartBanners(enabled);
    }

    @UsedByGodot
    public boolean isSmartBannersEnabled() {
        return Appodeal.isSmartBannersEnabled();
    }

    @UsedByGodot
    public void set728x90Banners(boolean enabled) {
        Appodeal.set728x90Banners(enabled);
    }

    @UsedByGodot
    public void setBannerAnimation(boolean animate) {
        Appodeal.setBannerAnimation(animate);
    }

    @UsedByGodot
    public void setBannerRotation(int leftBannerRotation, int rightBannerRotation) {
        Appodeal.setBannerRotation(leftBannerRotation, rightBannerRotation);
    }

    @UsedByGodot
    public void setUseSafeArea(boolean useSafeArea) {
        Appodeal.setUseSafeArea(useSafeArea);
    }

    @UsedByGodot
    public void trackInAppPurchase(float amount, String currency) {
        Appodeal.trackInAppPurchase(godotActivity, amount, currency);
    }

    @UsedByGodot
    public String[] getNetworks(int adType) {
        return Appodeal.getNetworks(godotActivity, getAndroidAdTypes(adType)).toArray(new String[0]);
    }

    @UsedByGodot
    public void disableNetwork(String network) {
        Appodeal.disableNetwork(network);
    }

    @UsedByGodot
    public void disableNetworkForAdTypes(String network, int adTypes) {
        Appodeal.disableNetwork(network, getAndroidAdTypes(adTypes));
    }

    @UsedByGodot
    public void setUserId(String userId) {
        Appodeal.setUserId(userId);
    }

    @UsedByGodot
    public String getUserId() {
        return Appodeal.getUserId();
    }

    @UsedByGodot
    public String getVersion() {
        return Appodeal.getVersion();
    }

    @UsedByGodot
    public String getPluginVersion() {
        return Appodeal.getPluginVersion();
    }

    @UsedByGodot
    public int getSegmentId() {
        return (int)Appodeal.getSegmentId();
    }

    @UsedByGodot
    public void setTesting(boolean testMode) {
        Appodeal.setTesting(testMode);
    }

    @UsedByGodot
    public void setLogLevel(int logLevel) {
        switch (logLevel) {
            case 0:
                Appodeal.setLogLevel(Log.LogLevel.verbose);
                break;
            case 1:
                Appodeal.setLogLevel(Log.LogLevel.debug);
                break;
            case 2:
            default:
                Appodeal.setLogLevel(Log.LogLevel.none);
        }
    }

    @UsedByGodot
    public void setFramework(String pluginVersion, String engineVersion) {
        Appodeal.setFramework("godot", pluginVersion, engineVersion);
    }

    @UsedByGodot
    public void setCustomFilterBool(String name, boolean value) {
        Appodeal.setCustomFilter(name, value);
    }

    @UsedByGodot
    public void setCustomFilterInt(String name, int value) {
        Appodeal.setCustomFilter(name, value);
    }

    @UsedByGodot
    public void setCustomFilterFloat(String name, float value) {
        Appodeal.setCustomFilter(name, value);
    }

    @UsedByGodot
    public void setCustomFilterString(String name, String value) {
        Appodeal.setCustomFilter(name, value);
    }

    @UsedByGodot
    public void resetCustomFilter(String name) {
        Appodeal.setCustomFilter(name, (Object)null);
    }

    @UsedByGodot
    public boolean canShow(int adType) {
        return Appodeal.canShow(getAndroidAdTypes(adType));
    }

    @UsedByGodot
    public boolean canShowForPlacement(int adType, String placementName) {
        return Appodeal.canShow(getAndroidAdTypes(adType), placementName);
    }

    @UsedByGodot
    public float getRewardAmount(String placementName) {
        return (float)Appodeal.getReward().getAmount();
    }

    @UsedByGodot
    public String getRewardCurrency(String placementName) {
        return Appodeal.getReward().getCurrency();
    }

    @UsedByGodot
    public void muteVideosIfCallsMuted(boolean isMuted) {
        Appodeal.muteVideosIfCallsMuted(isMuted);
    }

    @UsedByGodot
    public void disableWebViewCacheClear() {
        Appodeal.disableWebViewCacheClear();
    }

    @UsedByGodot
    public void startTestActivity() {
        Appodeal.startTestActivity(godotActivity);
    }

    @UsedByGodot
    public void setChildDirectedTreatment(boolean value) {
        Appodeal.setChildDirectedTreatment(value);
    }

    @UsedByGodot
    public void destroy(int adTypes) {
        Appodeal.destroy(getAndroidAdTypes(adTypes));
    }

    @UsedByGodot
    public void setExtraDataBool(String key, boolean value) {
        Appodeal.setExtraData(key, value);
    }

    @UsedByGodot
    public void setExtraDataInt(String key, int value) {
        Appodeal.setExtraData(key, value);
    }

    @UsedByGodot
    public void setExtraDataFloat(String key, float value) {
        Appodeal.setExtraData(key, value);
    }

    @UsedByGodot
    public void setExtraDataString(String key, String value) {
        Appodeal.setExtraData(key, value);
    }

    @UsedByGodot
    public void resetExtraData(String key) {
        Appodeal.setExtraData(key, (Object)null);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @UsedByGodot
    public float getPredictedEcpm (int adType) {
        return (float)Appodeal.getPredictedEcpm(getAndroidAdType(adType));
    }

    @UsedByGodot
    public void logEvent(String eventName, Dictionary params) {
        Map<String, Object> eventParams = new HashMap<>();

        String[] keys = params.get_keys();

        for(int i = 0; i < params.size(); i++) {
            String key = keys[i];
            Object val = params.get(key);
            if(val instanceof Integer) {
                eventParams.put(key, (Integer)val);
            }
            else if(val instanceof Float) {
                eventParams.put(key, (Float)val);
            }
            else if(val instanceof Boolean) {
                eventParams.put(key, (Boolean)val);
            }
            else if(val instanceof String) {
                eventParams.put(key, (String)val);
            }
        }

        Appodeal.logEvent(eventName, eventParams);
    }

    @UsedByGodot
    public void validatePlayStoreInAppPurchase(Dictionary payload) {
        Appodeal.validateInAppPurchase(godotActivity, getInAppPurchaseFromPayloadDictionary(payload), new  InAppPurchaseValidateCallback() {
            @Override
            public void onInAppPurchaseValidateSuccess(InAppPurchase inAppPurchase, List<ServiceError> list) {
                emitSignal(AppodealSignals.ON_INAPP_PURCHASE_VALIDATE_SUCCESS, getPurchaseInfo(inAppPurchase, list));
            }
            @Override
            public void onInAppPurchaseValidateFail(InAppPurchase inAppPurchase, List<ServiceError> list) {
                emitSignal(AppodealSignals.ON_INAPP_PURCHASE_VALIDATE_FAIL, getPurchaseInfo(inAppPurchase, list));
            }
        });
    }

    @UsedByGodot
    public void validateAppStoreInAppPurchase(Dictionary payload) {
        android.util.Log.d("AppodealGodot", "validateAppStoreInAppPurchase method is not supported on Android platform");
    }

    private int getAndroidAdType(int adType) {
        if((adType & 1) > 0) {
            return Appodeal.INTERSTITIAL;
        }
        if((adType & 2) > 0) {
            return Appodeal.BANNER;
        }
        if((adType & 4) > 0) {
            return Appodeal.REWARDED_VIDEO;
        }
        if((adType & 8) > 0) {
            return Appodeal.MREC;
        }
        if((adType & 16) > 0) {
            return Appodeal.NATIVE;
        }
        return Appodeal.NONE;
    }

    private int getAndroidAdTypes(int adTypes) {
        int nativeAdTypes = Appodeal.NONE;
        if((adTypes & 1) > 0) {
            nativeAdTypes |= Appodeal.INTERSTITIAL;
        }
        if((adTypes & 2) > 0) {
            nativeAdTypes |= Appodeal.BANNER;
        }
        if((adTypes & 4) > 0) {
            nativeAdTypes |= Appodeal.REWARDED_VIDEO;
        }
        if((adTypes & 8) > 0) {
            nativeAdTypes |= Appodeal.MREC;
        }
        if((adTypes & 16) > 0) {
            nativeAdTypes |= Appodeal.NATIVE;
        }
        return nativeAdTypes;
    }

    private int getAndroidShowStyle(int showStyle) {
        if((showStyle & 1) > 0) {
            return Appodeal.INTERSTITIAL;
        }
        if((showStyle & 2) > 0) {
            return Appodeal.BANNER_BOTTOM;
        }
        if((showStyle & 4) > 0) {
            return Appodeal.BANNER_TOP;
        }
        if((showStyle & 8) > 0) {
            return Appodeal.BANNER_LEFT;
        }
        if((showStyle & 16) > 0) {
            return Appodeal.BANNER_RIGHT;
        }
        if((showStyle & 32) > 0) {
            return Appodeal.REWARDED_VIDEO;
        }
        return Appodeal.NONE;
    }

    private int getAndroidYAxisPos(int viewPos) {
        if (viewPos == -1) return Appodeal.BANNER_BOTTOM;
        if (viewPos == -2) return Appodeal.BANNER_TOP;
        return viewPos;
    }

    private InAppPurchase getInAppPurchaseFromPayloadDictionary(Dictionary payload) {
        int purchaseType = 0;
        String publicKey = "";
        String signature = "";
        String purchaseData = "";
        String purchaseToken = "";
        int purchaseTimestamp = 0;
        String developerPayload = "";
        String orderId = "";
        String sku = "";
        String price = "";
        String currency = "";
        Map<String, String> additionalParams = new HashMap<>();

        String[] payloadKeys = payload.get_keys();
        for(int i = 0; i < payload.size(); i++) {
            String payloadKey = payloadKeys[i];
            Object payloadVal = payload.get(payloadKey);
            switch (payloadKey) {
                case "purchase_type":
                    purchaseType = payloadVal instanceof Integer ? (int)payloadVal : 0;
                    break;
                case "public_key":
                    publicKey = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "signature":
                    signature = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "purchase_data":
                    purchaseData = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "purchase_token":
                    purchaseToken = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "purchase_timestamp":
                    purchaseTimestamp = payloadVal instanceof Integer ? (int)payloadVal : 0;
                    break;
                case "developer_payload":
                    developerPayload = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "order_id":
                    orderId = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "sku":
                    sku = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "price":
                    price = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "currency":
                    currency = payloadVal instanceof String ? (String)payloadVal : "";
                    break;
                case "additional_parameters":
                    Dictionary params = payloadVal instanceof Dictionary ? (Dictionary)payloadVal : new Dictionary();
                    String[] paramsKeys = params.get_keys();
                    for(int j = 0; j < params.size(); j++) {
                        String paramKey = paramsKeys[j];
                        Object paramValue = params.get(paramKey);
                        if(paramValue instanceof String) {
                            additionalParams.put(paramKey, (String)paramValue);
                        }
                        else {
                            android.util.Log.d("AppodealGodot", "Payload -> additional_parameters values can only be Strings");
                        }
                    }
                    break;
                default:
                    android.util.Log.d("AppodealGodot", "incorrect payload key");
            }
        }

        return InAppPurchase.newBuilder(purchaseType == 0 ? InAppPurchase.Type.InApp : InAppPurchase.Type.Subs)
                .withPublicKey(publicKey)
                .withSignature(signature)
                .withPurchaseData(purchaseData)
                .withPurchaseToken(purchaseToken)
                .withPurchaseTimestamp(purchaseTimestamp)
                .withDeveloperPayload(developerPayload)
                .withOrderId(orderId)
                .withSku(sku)
                .withPrice(price)
                .withCurrency(currency)
                .withAdditionalParams(additionalParams)
                .build();
    }

    private String getPurchaseInfo(InAppPurchase inAppPurchase, List<ServiceError> list) {
        String responsePurchase = "\"InAppPurchase\":{";
        responsePurchase += String.format("\"Type\":\"%s\",", inAppPurchase.getType().toString());
        responsePurchase += String.format("\"PublicKey\":\"%s\",", inAppPurchase.getPublicKey());
        responsePurchase += String.format("\"Signature\":\"%s\",", inAppPurchase.getSignature());
        responsePurchase += String.format("\"PurchaseData\":\"%s\",", inAppPurchase.getPurchaseData());
        responsePurchase += String.format("\"PurchaseToken\":\"%s\",", inAppPurchase.getPurchaseToken());
        responsePurchase += String.format("\"PurchaseTimestamp\":%s,", inAppPurchase.getPurchaseTimestamp());
        responsePurchase += String.format("\"DeveloperPayload\":\"%s\",", inAppPurchase.getDeveloperPayload());
        responsePurchase += String.format("\"OrderId\":\"%s\",", inAppPurchase.getOrderId());
        responsePurchase += String.format("\"Sku\":\"%s\",", inAppPurchase.getSku());
        responsePurchase += String.format("\"Price\":\"%s\",", inAppPurchase.getPrice());
        responsePurchase += String.format("\"Currency\":\"%s\",", inAppPurchase.getCurrency());
        responsePurchase += String.format("\"AdditionalParameters\":%s}", inAppPurchase.getAdditionalParameters().toString());

        String responseError = "\"Errors\":[";
        if (list != null)
        {
            List<String> errorsList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++)
            {
                errorsList.add(String.format("\"%s\"", list.get(i).toString()));
            }
            responseError += String.join(",", errorsList);
        }
        responseError += ']';

        return String.format("{%s,%s}", responsePurchase, responseError);
    }

    private void setCallbacks() {
        setAdRevenueCallbacks();
        setMrecCallbacks();
        setBannerCallbacks();
        setInterstitialCallbacks();
        setRewardedVideoCallbacks();
    }

    private void setAdRevenueCallbacks() {
        Appodeal.setAdRevenueCallbacks(revenueInfo -> {
            Dictionary params = new Dictionary();
            params.put("ad_type", revenueInfo.getAdTypeString());
            params.put("network_name", revenueInfo.getNetworkName());
            params.put("ad_unit_name", revenueInfo.getAdUnitName());
            params.put("demand_source", revenueInfo.getDemandSource());
            params.put("placement", revenueInfo.getPlacement());
            params.put("revenue", (float)revenueInfo.getRevenue());
            params.put("currency", revenueInfo.getCurrency());
            params.put("revenue_precision", revenueInfo.getRevenuePrecision());

            emitSignal(AppodealSignals.ON_AD_REVENUE_RECEIVE, params);
        });
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void setMrecCallbacks() {
        Appodeal.setMrecCallbacks(new MrecCallbacks() {
            @Override
            public void onMrecLoaded(boolean isPrecache) {
                emitSignal(AppodealSignals.ON_MREC_LOADED, isPrecache);
            }
            @Override
            public void onMrecFailedToLoad() {
                emitSignal(AppodealSignals.ON_MREC_FAILED_TO_LOAD);
            }
            @Override
            public void onMrecShown() {
                emitSignal(AppodealSignals.ON_MREC_SHOWN);
            }
            @Override
            public void onMrecShowFailed() {
                emitSignal(AppodealSignals.ON_MREC_SHOW_FAILED);
            }
            @Override
            public void onMrecClicked() {
                emitSignal(AppodealSignals.ON_MREC_CLICKED);
            }
            @Override
            public void onMrecExpired() {
                emitSignal(AppodealSignals.ON_MREC_EXPIRED);
            }
        });
    }

    private void setBannerCallbacks() {
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {
                emitSignal(AppodealSignals.ON_BANNER_LOADED, height, isPrecache);
            }
            @Override
            public void onBannerFailedToLoad() {
                emitSignal(AppodealSignals.ON_BANNER_FAILED_TO_LOAD);
            }
            @Override
            public void onBannerShown() {
                emitSignal(AppodealSignals.ON_BANNER_SHOWN);
            }
            @Override
            public void onBannerShowFailed() {
                emitSignal(AppodealSignals.ON_BANNER_SHOW_FAILED);
            }
            @Override
            public void onBannerClicked() {
                emitSignal(AppodealSignals.ON_BANNER_CLICKED);
            }
            @Override
            public void onBannerExpired() {
                emitSignal(AppodealSignals.ON_BANNER_EXPIRED);
            }
        });
    }

    private void setInterstitialCallbacks() {
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_LOADED, isPrecache);
            }
            @Override
            public void onInterstitialFailedToLoad() {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_FAILED_TO_LOAD);
            }
            @Override
            public void onInterstitialShown() {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_SHOWN);
            }
            @Override
            public void onInterstitialShowFailed() {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_SHOW_FAILED);
            }
            @Override
            public void onInterstitialClicked() {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_CLICKED);
            }
            @Override
            public void onInterstitialClosed() {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_CLOSED);
            }
            @Override
            public void onInterstitialExpired()  {
                emitSignal(AppodealSignals.ON_INTERSTITIAL_EXPIRED);
            }
        });
    }

    private void setRewardedVideoCallbacks() {
        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_LOADED, isPrecache);
            }
            @Override
            public void onRewardedVideoFailedToLoad() {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_FAILED_TO_LOAD);
            }
            @Override
            public void onRewardedVideoShown() {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_SHOWN);
            }
            @Override
            public void onRewardedVideoShowFailed() {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_SHOW_FAILED);
            }
            @Override
            public void onRewardedVideoClicked() {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_CLICKED);
            }
            @Override
            public void onRewardedVideoFinished(double amount, String name) {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_FINISHED, (float)amount, name == null ? "" : name);
            }
            @Override
            public void onRewardedVideoClosed(boolean finished) {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_CLOSED, finished);
            }
            @Override
            public void onRewardedVideoExpired() {
                emitSignal(AppodealSignals.ON_REWARDED_VIDEO_EXPIRED);
            }
        });
    }

}
