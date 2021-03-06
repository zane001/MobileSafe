package com.android.internal.telephony;

import android.os.Bundle;
import java.util.List;
import android.telephony.NeighboringCellInfo;

interface ITelephony {
    void dial(String number);
    void call(String number);
    boolean showCallScreen();
    boolean showCallScreenWithDialpad(boolean showDialpad);
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
    boolean isOffhook();
    boolean isRinging();
    boolean isIdle();
    boolean isRadioOn();
    boolean isSimPinEnabled();
    void cancelMissedCallsNotification();
    boolean supplyPin(String pin);
    boolean handlePinMmi(String dialString);
    void toggleRadioOnOff();
    boolean setRadio(boolean turnOn);
    void updateServiceLocation();
    void enableLocationUpdates();
    void disableLocationUpdates();
    int enableApnType(String type);
    int disableApnType(String type);
    boolean enableDataConnectivity();
    boolean disableDataConnectivity();
    boolean isDataConnectivityPossible();
    Bundle getCellLocation();
    List<NeighboringCellInfo> getNeighboringCellInfo();
    int getCallState();
    int getDataActivity();
    int getDataState();
    int getActivePhoneType();
    int getCdmaEriIconIndex();
    int getCdmaEriIconMode();
    String getCdmaEriText();
    boolean getCdmaNeedsProvisioning();
    int getVoiceMessageCount();
    int getNetworkType();
    boolean hasIccCard();
}
