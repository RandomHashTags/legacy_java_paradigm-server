package me.randomhashtags.worldlaws.smartphones;

public final class SmartphoneRearCamera {

    private String cameras, apertures, opticalImageStabilization, autoImageStabilization, lenses, nightModes, panorama, portraitModes, portraitLighting, lensCover;
    private boolean autoAdjustment, burstMode, livePhotos, wideColorCapture;
    private String flash, hdr, videoRecording, extendedDynamicRangeVideo, opticalImageStabilizationForVideo, opticalZoom, digitalZoom, autofocus, opticalZoomVideo, digitalZoomVideo, slowMotionVideo;
    private boolean audioZoom, timeLapseVideoWithStabilization, studioRecording;
    private String quickTakeVideo, cinematicVideoStabilization;

    public SmartphoneRearCamera() {
    }

    public void setCameras(String cameras) {
        this.cameras = cameras;
    }

    public void setApertures(String apertures) {
        this.apertures = apertures;
    }

    public void setOpticalImageStabilization(String opticalImageStabilization) {
        this.opticalImageStabilization = opticalImageStabilization;
    }

    public void setAutoImageStabilization(String autoImageStabilization) {
        this.autoImageStabilization = autoImageStabilization;
    }

    public String getLenses() {
        return lenses;
    }
    public void setLenses(String lenses) {
        this.lenses = lenses;
    }

    public String getNightModes() {
        return nightModes;
    }
    public void setNightModes(String nightModes) {
        this.nightModes = nightModes;
    }

    public boolean hasAutoAdjustment() {
        return autoAdjustment;
    }
    public void setAutoAdjustment(boolean autoAdjustment) {
        this.autoAdjustment = autoAdjustment;
    }

    public String getOpticalZoom() {
        return opticalZoom;
    }
    public void setOpticalZoom(String opticalZoom) {
        this.opticalZoom = opticalZoom;
    }

    public String getDigitalZoom() {
        return digitalZoom;
    }
    public void setDigitalZoom(String digitalZoom) {
        this.digitalZoom = digitalZoom;
    }

    public String getAutofocus() {
        return autofocus;
    }
    public void setAutofocus(String autofocus) {
        this.autofocus = autofocus;
    }

    public String getPanorama() {
        return panorama;
    }
    public void setPanorama(String panorama) {
        this.panorama = panorama;
    }

    public String getPortraitModes() {
        return portraitModes;
    }
    public void setPortraitModes(String portraitModes) {
        this.portraitModes = portraitModes;
    }

    public String getPortraitLighting() {
        return portraitLighting;
    }
    public void setPortraitLighting(String portraitLighting) {
        this.portraitLighting = portraitLighting;
    }

    public String getLensCover() {
        return lensCover;
    }
    public void setLensCover(String lensCover) {
        this.lensCover = lensCover;
    }

    public boolean hasBurstMode() {
        return burstMode;
    }
    public void setBurstMode(boolean burstMode) {
        this.burstMode = burstMode;
    }

    public String getFlash() {
        return flash;
    }
    public void setFlash(String flash) {
        this.flash = flash;
    }

    public boolean hasLivePhotos() {
        return livePhotos;
    }
    public void setLivePhotos(boolean livePhotos) {
        this.livePhotos = livePhotos;
    }

    public boolean hasWideColorCapture() {
        return wideColorCapture;
    }
    public void setWideColorCapture(boolean wideColorCapture) {
        this.wideColorCapture = wideColorCapture;
    }

    public String getHDRForPhotos() {
        return hdr;
    }
    public void setHDRForPhotos(String hdr) {
        this.hdr = hdr;
    }

    public String getVideoRecording() {
        return videoRecording;
    }
    public void setVideoRecording(String videoRecording) {
        this.videoRecording = videoRecording;
    }

    public String getExtendedDynamicRangeVideo() {
        return extendedDynamicRangeVideo;
    }
    public void setExtendedDynamicRangeVideo(String extendedDynamicRangeVideo) {
        this.extendedDynamicRangeVideo = extendedDynamicRangeVideo;
    }

    public String getOpticalImageStabilizationForVideo() {
        return opticalImageStabilizationForVideo;
    }
    public void setOpticalImageStabilizationForVideo(String opticalImageStabilizationForVideo) {
        this.opticalImageStabilizationForVideo = opticalImageStabilizationForVideo;
    }

    public String getOpticalZoomForVideo() {
        return opticalZoomVideo;
    }
    public void setOpticalZoomForVideo(String opticalZoomVideo) {
        this.opticalZoomVideo = opticalZoomVideo;
    }

    public String getDigitalZoomForVideo() {
        return digitalZoomVideo;
    }
    public void setDigitalZoomForVideo(String digitalZoomVideo) {
        this.digitalZoomVideo = digitalZoomVideo;
    }

    public String getSlowMotionVideo() {
        return slowMotionVideo;
    }
    public void setSlowMotionVideo(String slowMotionVideo) {
        this.slowMotionVideo = slowMotionVideo;
    }

    public boolean hasAudioZoom() {
        return audioZoom;
    }
    public void setAudioZoom(boolean audioZoom) {
        this.audioZoom = audioZoom;
    }

    public String getQuickTakeVideo() {
        return quickTakeVideo;
    }
    public void setQuickTakeVideo(String quickTakeVideo) {
        this.quickTakeVideo = quickTakeVideo;
    }


    @Override
    public String toString() {
        return "{" +
                "\"cameras\":\"" + cameras + "\"," +
                "\"apertures\":\"" + apertures + "\"," +
                "\"opticalImageStabilization\":\"" + opticalImageStabilization + "\"," +
                "\"autoImageStabilization\":\"" + autoImageStabilization + "\"," +
                "\"lenses\":\"" + lenses + "\"," +
                "\"nightModes\":\"" + nightModes + "\"," +
                "\"hasAutoAdjustment\":" + autoAdjustment + "," +
                "\"opticalZoom\":\"" + opticalZoom + "\"," +
                "\"digitalZoom\":\"" + digitalZoom + "\"," +
                "\"autofocus\":\"" + autofocus + "\"," +
                "\"panorama\":\"" + panorama + "\"," +
                "\"portraitModes\":\"" + portraitModes + "\"," +
                "\"portraitLighting\":\"" + portraitLighting + "\"," +
                "\"lensCover\":\"" + lensCover + "\"," +
                "\"hasBurstMode\":" + burstMode + "," +
                "\"flash\":\"" + flash + "\"," +
                "\"hasLivePhotos\":" + livePhotos + "," +
                "\"hasWideColorCapture\":" + wideColorCapture + "," +
                "\"hdrForPhotos\":\"" + hdr + "\"," +
                "\"videoRecording\":\"" + videoRecording + "\"," +
                "\"extendedDynamicRangeVideo\":\"" + extendedDynamicRangeVideo + "\"," +
                "\"opticalImageStabilizationForVideo\":\"" + opticalImageStabilizationForVideo + "\"," +
                "\"opticalZoomForVideo\":\"" + opticalZoomVideo + "\"," +
                "\"digitalZoomForVideo\":\"" + digitalZoomVideo + "\"," +
                "\"slowMotionVideo\":\"" + slowMotionVideo + "\"," +
                "\"hasAudioZoom\":" + audioZoom + "," +
                "\"quickTakeVideo\":\"" + quickTakeVideo + "\"," +
                "\"hasTimeLapseVideoWithStabilization\":" + timeLapseVideoWithStabilization + "," +
                "\"cinematicVideoStabilization\":\"" + cinematicVideoStabilization + "\"," +
                "\"hasStudioRecording\":" + studioRecording +
                "}";
    }
}
