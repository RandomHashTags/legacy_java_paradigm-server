package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PhonesApple implements SmartphoneCompany {
    INSTANCE;

    private String modelsList;
    private HashMap<String, String> models;
    private Elements geekbenchSingleCore, geekbenchMultiCore, geekbenchMetal;

    @Override
    public String getBackendID() {
        return "apple";
    }

    @Override
    public void getSmartphoneListJSON(CompletionHandler handler) {
        if(modelsList != null) {
            handler.handleString(modelsList);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(modelsList);
                }
            });
        }
    }

    @Override
    public void getSmartphoneDetails(String model, CompletionHandler handler) {
        if(models != null) {
            handler.handleString(models.get(model));
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(models.get(model));
                }
            });
        }
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        models = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_iOS_and_iPadOS_devices";
        final Document doc = getDocument(url);
        final StringBuilder builder = new StringBuilder("[");
        if(doc != null) {
            final Document geekbench = getDocument("https://browser.geekbench.com/ios-benchmarks");
            final Elements phones = geekbench.select("div.col-md-9 div.tabbable div.tab-content div.tab-pane div.table-responsive table.table");
            geekbenchSingleCore = phones.get(0).select("tbody tr");
            geekbenchMultiCore = phones.get(1).select("tbody tr");
            geekbenchMetal = phones.get(2).select("tbody tr");

            final String brand = getBackendID();
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            final List<Smartphone> smartphones = getPhonesFromTable(tables.get(2), url, true);
            smartphones.addAll(getPhonesFromTable(tables.get(3), url, false));
            smartphones.addAll(getPhonesFromTable(tables.get(4), url, false));
            boolean isFirst = true;
            for(Smartphone smartphone : smartphones) {
                final String model = smartphone.getDetail(SmartphoneDetailType.MODEL);
                final String backendID = smartphone.getDetail(SmartphoneDetailType.MODEL_BACKEND_ID);
                models.put(backendID, smartphone.toString());
                final PreSmartphone preSmartphone = new PreSmartphone(brand, backendID, model);
                builder.append(isFirst ? "" : ",").append(preSmartphone.toString());
                isFirst = false;
            }
        }
        builder.append("]");
        final String string = builder.toString();
        modelsList = string;
        WLLogger.logInfo("PhonesApple - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handleString(string);
    }
    private List<Smartphone> getPhonesFromTable(Element table, String url, boolean hasLIDAR) {
        final String brand = getBackendID();
        final List<Smartphone> smartphones = new ArrayList<>();

        final Elements elements = table.select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: List of iOS and iPadOS devices", url);
        final HashMap<Integer, Smartphone> builders = new HashMap<>();
        final HashMap<Integer, EventSources> sources = new HashMap<>();

        final Elements models = elements.get(0).select("th a");
        final int maxPhones = models.size();
        int index = 0;
        for(Element element : models) {
            final String model = element.text(), wiki = "https://en.wikipedia.org" + element.attr("href");
            final Smartphone builder = new Smartphone();
            builder.setDetail(SmartphoneDetailType.BRAND, brand);
            builder.setDetail(SmartphoneDetailType.MODEL_BACKEND_ID, model.toLowerCase().replace(" ", ""));
            builder.setDetail(SmartphoneDetailType.MODEL, model);
            builders.put(index, builder);
            sources.put(index, new EventSources(new EventSource("Wikipedia: " + model, wiki)));
            index++;
        }

        index = 0;
        elements.remove(0);
        final Elements images = elements.get(0).select("td a img");
        for(Element element : images) {
            final String imageURL = "https:" + element.attr("src");
            builders.get(index).setDetail(SmartphoneDetailType.IMAGE_URL, imageURL);
            index++;
        }
        elements.remove(0);

        final List<String> initialReleaseOperatingSystems = getRowValues(elements.get(0).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String os = initialReleaseOperatingSystems.get(i);
            builders.get(i).setDetail(SmartphoneDetailType.OPERATING_SYSTEM_INITIAL, os);
        }
        elements.remove(0);

        final List<String> latestReleaseOperatingSystems = getRowValues(elements.get(0).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String os = latestReleaseOperatingSystems.get(i);
            builders.get(i).setDetail(SmartphoneDetailType.OPERATING_SYSTEM_LATEST, os);
        }
        elements.remove(0);

        final HashMap<Integer, SmartphoneDisplay> displays = new HashMap<>();
        final List<String> displayScreenSize = getRowValues(elements.get(0).select("tr td"));
        final List<String> displayMultitouch = getRowValues(elements.get(2).select("tr td"));
        final List<String> displayTechnology = getRowValues(elements.get(3).select("tr td"));
        final List<String> displayResolution = getRowValues(elements.get(4).select("tr td"));
        final List<String> displayPixelsPerInch = getRowValues(elements.get(5).select("tr td"));
        final List<String> displayAspectRatio = getRowValues(elements.get(6).select("tr td"));
        final List<String> displayMaxNitBrightness = getRowValues(elements.get(7).select("tr td"));
        final List<String> displayFingerprintResistantOC = getRowValues(elements.get(8).select("tr td"));
        final List<String> displayContrastRatio = getRowValues(elements.get(9).select("tr td"));
        final List<String> displayFullSRGB = getRowValues(elements.get(10).select("tr td"));
        final List<String> displayWideColor = getRowValues(elements.get(11).select("tr td"));
        final List<String> displayTrueTone = getRowValues(elements.get(12).select("tr td"));
        final List<String> displayHDR = getRowValues(elements.get(13).select("tr td"));
        final List<String> displayHDR10 = getRowValues(elements.get(14).select("tr td"));
        final List<String> displayDolbyVision = getRowValues(elements.get(15).select("tr td"));
        final List<String> displayTaptic = getRowValues(elements.get(16).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final SmartphoneDisplay display = new SmartphoneDisplay();
            final boolean multitouch = displayMultitouch.get(i).equalsIgnoreCase("yes");
            final String technology = displayTechnology.get(i), resolution = displayResolution.get(i);
            final int pixelsPerInch = Integer.parseInt(displayPixelsPerInch.get(i));
            final String aspectRatio = displayAspectRatio.get(i);
            final String maxNitBrightness = displayMaxNitBrightness.get(i);
            final boolean fingerprintResistantOC = displayFingerprintResistantOC.get(i).equalsIgnoreCase("yes");
            final String contrastRatio = displayContrastRatio.get(i);
            final boolean fullSRGB = displayFullSRGB.get(i).equalsIgnoreCase("yes");
            final boolean wideColor = displayWideColor.get(i).equalsIgnoreCase("yes");
            final boolean truetone = displayTrueTone.get(i).equalsIgnoreCase("yes");
            final boolean hdr = displayHDR.get(i).equalsIgnoreCase("yes");
            final boolean hdr10 = displayHDR10.get(i).equalsIgnoreCase("yes");
            final boolean dolbyVision = displayDolbyVision.get(i).equalsIgnoreCase("yes");
            final String taptic = displayTaptic.get(i);
            display.setMultitouch(multitouch);
            display.setTechnology(technology);
            display.setResolution(resolution);
            display.setPixelsPerInch(pixelsPerInch);
            display.setAspectRatio(aspectRatio);
            display.setMaxNitBrightness(maxNitBrightness);
            display.setFingerprintResistantOleophobicCoating(fingerprintResistantOC);
            display.setContrastRatio(contrastRatio);
            display.setFullSRGB(fullSRGB);
            display.setWideColor(wideColor);
            display.setTruetone(truetone);
            display.setHDR(hdr);
            display.setHDR10(hdr10);
            display.setDolbyVision(dolbyVision);
            display.setTaptics(taptic);
            displays.put(i, display);
        }
        for(int i = 1; i <= 17; i++) {
            elements.remove(0);
        }

        // processor
        final HashMap<Integer, Processor> processors = new HashMap<>();
        final List<String> processorNames = getRowValues(elements.get(0).select("tr td"));
        final List<String> processorNanometerTechnology = getRowValues(elements.get(1).select("tr td"));
        final List<String> processorTotalCores = getRowValues(elements.get(2).select("tr td"));
        final List<String> processorHighPerformanceCores = getRowValues(elements.get(3).select("tr td"));
        final List<String> processorEnergyEfficiencyCores = getRowValues(elements.get(4).select("tr td"));
        final List<String> processorClockSpeed = getRowValues(elements.get(5).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String name = processorNames.get(i);
            final String nanometerTechnology = processorNanometerTechnology.get(i);
            final String totalCores = processorTotalCores.get(i);
            final String highPerformanceCores = processorHighPerformanceCores.get(i);
            final String energyEfficiencyCores = processorEnergyEfficiencyCores.get(i);
            final String clockSpeed = processorClockSpeed.get(i);
            final SmartphoneProcessor processor = new SmartphoneProcessor(name, nanometerTechnology, totalCores, highPerformanceCores, energyEfficiencyCores, clockSpeed);
            processors.put(i, processor);
        }
        for(int i = 1; i <= 11; i++) {
            elements.remove(0);
        }

        final List<String> storages = getRowValues(elements.get(0).select("tr td"));
        final List<String> storageTypes = getRowValues(elements.get(1).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String storage = storages.get(i), storageType = storageTypes.get(i);
            builders.get(i).setDetail(SmartphoneDetailType.STORAGE_OPTIONS, storage);
            builders.get(i).setDetail(SmartphoneDetailType.STORAGE_TYPE, storageType);
        }
        for(int i = 1; i <= 2; i++) {
            elements.remove(0);
        }

        final List<String> totalRam = getRowValues(elements.get(0).select("tr td"));
        final List<String> ramTypes = getRowValues(elements.get(1).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String ram = totalRam.get(i), ramType = ramTypes.get(i);
            final Smartphone smartphone = builders.get(i);
            smartphone.setDetail(SmartphoneDetailType.RAM_TOTAL, ram);
            smartphone.setDetail(SmartphoneDetailType.RAM_TYPE, ramType);
        }
        elements.remove(0);
        elements.remove(0);

        elements.remove(0); // connector

        final List<String> connectivityWIFI = getRowValues(elements.get(0).select("tr td"));
        final List<String> connectivityMIMO = getRowValues(elements.get(1).select("tr td"));
        final List<String> connectivityNFC = getRowValues(elements.get(2).select("tr td"));
        final List<String> connectivityExpressCards = getRowValues(elements.get(3).select("tr td"));
        final List<String> connectivityBluetooth = getRowValues(elements.get(4).select("tr td"));
        final List<String> connectivityUltraWidebandChip = getRowValues(elements.get(5).select("tr td"));
        final List<String> connectivityCellular = getRowValues(elements.get(6).select("tr td"));
        final List<String> connectivityVoLTE = getRowValues(elements.get(7).select("tr td"));
        final List<String> connectivityAssistedGPS = getRowValues(elements.get(8).select("tr td"));
        final List<String> connectivityGLONASS = getRowValues(elements.get(9).select("tr td"));
        final List<String> connectivitySIMCard1 = getRowValues(elements.get(10).select("tr td"));
        final List<String> connectivitySIMCard2 = getRowValues(elements.get(11).select("tr td"));
        final HashMap<Integer, SmartphoneConnectivity> connectivities = new HashMap<>();
        for(int i = 0; i < maxPhones; i++) {
            final SmartphoneConnectivity connectivity = new SmartphoneConnectivity();
            connectivity.setWIFI(connectivityWIFI.get(i));
            connectivity.setMIMO(connectivityMIMO.get(i).equalsIgnoreCase("yes"));
            connectivity.setNFC(connectivityNFC.get(i));
            connectivities.put(i, connectivity);
        }
        for(int i = 1; i <= 12; i++) {
            elements.remove(0);
        }

        final List<String> authenticationFingerprint = getRowValues(elements.get(0).select("tr td"));
        final List<String> authenticationFace = getRowValues(elements.get(1).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final boolean fingerprint = authenticationFingerprint.get(i).equalsIgnoreCase("yes"), face = authenticationFace.get(i).equalsIgnoreCase("yes");
            final Smartphone smartphone = builders.get(i);
            smartphone.setDetail(SmartphoneDetailType.AUTHENTICATION_FINGERPRINT, "" + fingerprint);
            smartphone.setDetail(SmartphoneDetailType.AUTHENTICATION_FACE, "" + face);
        }
        for(int i = 1; i <= 2; i++) {
            elements.remove(0);
        }

        final HashMap<Integer, Sensors> sensors = getSensors(maxPhones, hasLIDAR, elements);

        // rear camera
        final List<String> rearCameras = getRowValues(elements.get(0).select("tr td"));
        final List<String> rearCameraAperture = getRowValues(elements.get(1).select("tr td"));
        final List<String> rearCameraOpticalImageStabilization = getRowValues(elements.get(2).select("tr td"));
        final List<String> rearCameraAutoImageStabilization = getRowValues(elements.get(3).select("tr td"));
        final List<String> rearCameraLenses = getRowValues(elements.get(4).select("tr td"));
        final List<String> rearCameraNightMode = getRowValues(elements.get(5).select("tr td"));
        final List<String> rearCameraAutoAdjustment = getRowValues(elements.get(6).select("tr td"));
        final List<String> rearCameraOpticalZoom = getRowValues(elements.get(7).select("tr td"));
        final List<String> rearCameraDigitalZoom = getRowValues(elements.get(8).select("tr td"));
        final List<String> rearCameraAutofocus = getRowValues(elements.get(9).select("tr td"));
        final List<String> rearCameraPanorama = getRowValues(elements.get(10).select("tr td"));
        final List<String> rearCameraPortraitMode = getRowValues(elements.get(11).select("tr td"));
        final List<String> rearCameraPortraitLighting = getRowValues(elements.get(12).select("tr td"));
        final List<String> rearCameraLensCover = getRowValues(elements.get(13).select("tr td"));
        final List<String> rearCameraBurstMode = getRowValues(elements.get(14).select("tr td"));
        final List<String> rearCameraFlash = getRowValues(elements.get(15).select("tr td"));
        final List<String> rearCameraLivePhotos = getRowValues(elements.get(16).select("tr td"));
        final List<String> rearCameraWideColorCapture = getRowValues(elements.get(17).select("tr td"));
        final List<String> rearCameraWideHDR = getRowValues(elements.get(18).select("tr td"));
        final List<String> rearCameraVideoRecording = getRowValues(elements.get(19).select("tr td"));
        final List<String> rearCameraExtendedDynamicRangeVideo = getRowValues(elements.get(20).select("tr td"));
        final List<String> rearCameraExtendedOpticalImageStabilizationForVideo = getRowValues(elements.get(21).select("tr td"));
        final List<String> rearCameraOpticalZoomVideo = getRowValues(elements.get(22).select("tr td"));
        final List<String> rearCameraDigitalZoomVideo = getRowValues(elements.get(23).select("tr td"));
        final List<String> rearCameraSlowMotionVideo = getRowValues(elements.get(24).select("tr td"));
        final List<String> rearCameraAudioZoom = getRowValues(elements.get(25).select("tr td"));
        final List<String> rearCameraQuickTakeVideo = getRowValues(elements.get(26).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String cameras = rearCameras.get(i), apertures = rearCameraAperture.get(i), opticalImageStabilization = rearCameraOpticalImageStabilization.get(i);
            final String autoImageStabilization = rearCameraAutoImageStabilization.get(i), lenses = rearCameraLenses.get(i), nightModes = rearCameraNightMode.get(i);
            final boolean autoAdjustment = rearCameraAutoAdjustment.get(i).equalsIgnoreCase("yes");
            final String opticalZoom = rearCameraOpticalZoom.get(i), digitalZoom = rearCameraDigitalZoom.get(i), autofocus = rearCameraAutofocus.get(i), panorama = rearCameraPanorama.get(i);
            final String portraitModes = rearCameraPortraitMode.get(i), portraitLighting = rearCameraPortraitLighting.get(i), lensCover = rearCameraLensCover.get(i);
            final boolean burstMode = rearCameraBurstMode.get(i).equalsIgnoreCase("yes"), livePhotos = rearCameraLivePhotos.get(i).equalsIgnoreCase("yes"), wideColorCapture = rearCameraWideColorCapture.get(i).equalsIgnoreCase("yes");
            final String flash = rearCameraFlash.get(i);
            final String hdr = rearCameraWideHDR.get(i), videoRecording = rearCameraVideoRecording.get(i), extendedDynamicRangeVideo = rearCameraExtendedDynamicRangeVideo.get(i), opticalImageStabilizationForVideo = rearCameraExtendedOpticalImageStabilizationForVideo.get(i);
            final String opticalZoomVideo = rearCameraOpticalZoomVideo.get(i), digitalZoomVideo = rearCameraDigitalZoomVideo.get(i), slowMotionVideo = rearCameraSlowMotionVideo.get(i);
            final boolean audioZoom = rearCameraAudioZoom.get(i).equalsIgnoreCase("yes");
            final String quickTakeVideo = rearCameraQuickTakeVideo.get(i);
            final Smartphone smartphone = builders.get(i);
            final SmartphoneRearCamera camera = new SmartphoneRearCamera();
            camera.setCameras(cameras);
            camera.setApertures(apertures);
            camera.setOpticalImageStabilization(opticalImageStabilization);
            camera.setAutoImageStabilization(autoImageStabilization);
            camera.setLenses(lenses);
            camera.setNightModes(nightModes);
            camera.setAutoAdjustment(autoAdjustment);
            camera.setOpticalZoom(opticalZoom);
            camera.setDigitalZoom(digitalZoom);
            camera.setAutofocus(autofocus);
            camera.setPanorama(panorama);
            camera.setPortraitModes(portraitModes);
            camera.setPortraitLighting(portraitLighting);
            camera.setLensCover(lensCover);
            camera.setBurstMode(burstMode);
            camera.setFlash(flash);
            camera.setLivePhotos(livePhotos);
            camera.setWideColorCapture(wideColorCapture);
            camera.setHDRForPhotos(hdr);
            camera.setVideoRecording(videoRecording);
            camera.setExtendedDynamicRangeVideo(extendedDynamicRangeVideo);
            camera.setOpticalImageStabilizationForVideo(opticalImageStabilizationForVideo);
            camera.setOpticalZoomForVideo(opticalZoomVideo);
            camera.setDigitalZoomForVideo(digitalZoomVideo);
            camera.setSlowMotionVideo(slowMotionVideo);
            camera.setAudioZoom(audioZoom);
            camera.setQuickTakeVideo(quickTakeVideo);
            smartphone.setDetail(SmartphoneDetailType.CAMERA_REAR, camera.toString());
        }
        for(int i = 1; i <= 30; i++) {
            elements.remove(0);
        }

        // front camera
        for(int i = 1; i <= 15; i++) {
            elements.remove(0);
        }

        /// audio
        for(int i = 1; i <= 3; i++) {
            elements.remove(0);
        }

        elements.remove(0); // HAC Rating
        elements.remove(0); // compatible with Mode for iPhone Hearings Airs
        elements.remove(0); // live listen

        // materials
        for(int i = 1; i <= 3; i++) {
            elements.remove(0);
        }

        elements.remove(0); // colors
        final List<String> powers = getRowValues(elements.get(0).select("tr td"));
        final List<String> fastChargings = getRowValues(elements.get(1).select("tr td"));
        final List<String> wirelessChargings = getRowValues(elements.get(2).select("tr td"));
        final List<String> resistances = getRowValues(elements.get(3).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String power = powers.get(i).split("\\[")[0], fastCharging = fastChargings.get(i).split("\\[")[0], wirelessCharging = wirelessChargings.get(i), resistance = resistances.get(i);
            final Smartphone smartphone = builders.get(i);
            smartphone.setDetail(SmartphoneDetailType.POWER, power);
            smartphone.setDetail(SmartphoneDetailType.FAST_CHARGING, fastCharging);
            smartphone.setDetail(SmartphoneDetailType.WIRELESS_CHARGING, wirelessCharging);
            smartphone.setDetail(SmartphoneDetailType.RESISTANCE, resistance);
        }
        for(int i = 1; i <= 4; i++) {
            elements.remove(0);
        }

        final List<String> heights = getRowValues(elements.get(0).select("tr td"));
        final List<String> widths = getRowValues(elements.get(1).select("tr td"));
        final List<String> depths = getRowValues(elements.get(2).select("tr td"));
        for(int i = 0; i < maxPhones; i++) {
            final String height = heights.get(i), width = widths.get(i), depth = depths.get(i);
            final Smartphone smartphone = builders.get(i);
            final String dimensions = new SmartphoneDimensions(height, width, depth).toString();
            smartphone.setDetail(SmartphoneDetailType.DIMENSIONS, dimensions);
        }

        final EventSource geekbench = new EventSource("Geekbench: iOS Benchmark Charts", "https://browser.geekbench.com/ios-benchmarks");
        for(Map.Entry<Integer, Smartphone> map : builders.entrySet()) {
            index = map.getKey();
            final EventSources eventSources = sources.get(index);
            eventSources.append(wikipedia);
            eventSources.append(geekbench);
            final Smartphone smartphone = map.getValue();
            final String model = smartphone.getDetail(SmartphoneDetailType.MODEL);
            final int singleCore = getGeekbenchSingleCore(model), multiCore = getGeekbenchMultiCore(model), metal = getGeekbenchMetal(model);
            final GeekbenchScores geekbenchScores = new GeekbenchScores(singleCore, multiCore, metal);
            smartphone.setDetail(SmartphoneDetailType.GEEKBENCH_SCORES, geekbenchScores.toString());
            smartphone.setDetail(SmartphoneDetailType.SOURCES, eventSources.toString());
            smartphone.setDetail(SmartphoneDetailType.DISPLAY, displays.get(index).toString());
            smartphone.setDetail(SmartphoneDetailType.SENSORS, sensors.get(index).toString());
            smartphone.setDetail(SmartphoneDetailType.PROCESSOR, processors.get(index).toString());
            smartphone.setDetail(SmartphoneDetailType.CONNECTIVITY, connectivities.get(index).toString());
            smartphones.add(smartphone);
        }
        return smartphones;
    }
    private int getGeekbenchSingleCore(String model) {
        return getGeekbenchScore(geekbenchSingleCore, model);
    }
    private int getGeekbenchMultiCore(String model) {
        return getGeekbenchScore(geekbenchMultiCore, model);
    }
    private int getGeekbenchMetal(String model) {
        return getGeekbenchScore(geekbenchMetal, model);
    }
    private int getGeekbenchScore(Elements elements, String model) {
        final List<Element> set = new ArrayList<>(elements);
        set.removeIf(element -> !element.select("td.name a").get(0).text().equalsIgnoreCase(model));
        return set.isEmpty() ? -1 : Integer.parseInt(set.get(0).select("td.score").text());
    }

    private HashMap<Integer, Sensors> getSensors(int maxPhones, boolean hasLidar, Elements elements) {
        final HashMap<Integer, Sensors> sensors = new HashMap<>();
        final List<String> sensorsFirst = getRowValues(elements.get(0).select("tr td"));
        final List<String> sensorsSecond = getRowValues(elements.get(1).select("tr td"));
        final List<String> sensorsThird = getRowValues(elements.get(2).select("tr td"));
        final List<String> sensorsFourth = getRowValues(elements.get(3).select("tr td"));
        final List<String> sensorsFifth = getRowValues(elements.get(4).select("tr td"));
        final List<String> sensorsSixth = hasLidar ? getRowValues(elements.get(5).select("tr td")) : null;
        for(int i = 0; i < maxPhones; i++) {
            final boolean firstSensor = sensorsFirst.get(i).equalsIgnoreCase("yes");
            final boolean secondSensor = sensorsSecond.get(i).equalsIgnoreCase("yes");
            final boolean thirdSensor = sensorsThird.get(i).equalsIgnoreCase("yes");
            final boolean fourthSensor = sensorsFourth.get(i).equalsIgnoreCase("yes");
            final boolean fifthSensor = sensorsFifth.get(i).equalsIgnoreCase("yes");
            final boolean sixthSensor = hasLidar && sensorsSixth.get(i).equalsIgnoreCase("yes");
            final SmartphoneSensors sensor = new SmartphoneSensors();
            sensor.setLidarSensor(hasLidar && firstSensor);
            sensor.setProximitySensor(hasLidar ? secondSensor : firstSensor);
            sensor.setThreeAxisGyro(hasLidar ? thirdSensor : secondSensor);
            sensor.setAccelerometer(hasLidar ? fourthSensor : thirdSensor);
            sensor.setAmbientLightSensor(hasLidar ? fifthSensor : fourthSensor);
            sensor.setBarometer(hasLidar ? sixthSensor : fifthSensor);
            sensors.put(i, sensor);
        }
        final int amount = hasLidar ? 6 : 5;
        for(int i = 1; i <= amount; i++) {
            elements.remove(0);
        }
        return sensors;
    }
}
