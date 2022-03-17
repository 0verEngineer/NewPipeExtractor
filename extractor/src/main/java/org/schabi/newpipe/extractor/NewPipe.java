package org.schabi.newpipe.extractor;

/*
 * Created by Christian Schabesberger on 23.08.15.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * NewPipe.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Provides access to streaming services supported by NewPipe.
 */
public final class NewPipe {
    private static Downloader downloader;
    private static Localization preferredLocalization;
    private static ContentCountry preferredContentCountry;

    private NewPipe() {
    }

    public static void init(final Downloader d) {
        downloader = d;
        preferredLocalization = Localization.DEFAULT;
        preferredContentCountry = ContentCountry.DEFAULT;
    }

    public static void init(final Downloader d, final Localization l) {
        downloader = d;
        preferredLocalization = l;
        preferredContentCountry = l.getCountryCode().isEmpty()
                ? ContentCountry.DEFAULT : new ContentCountry(l.getCountryCode());
    }

    public static void init(final Downloader d, final Localization l, final ContentCountry c) {
        downloader = d;
        preferredLocalization = l;
        preferredContentCountry = c;
    }

    public static Downloader getDownloader() {
        return downloader;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    public static List<StreamingService> getServices() {
        return ServiceList.all();
    }

    public static StreamingService getService(int serviceId) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceId() == serviceId) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the id = \"" + serviceId + "\"");
    }

    public static StreamingService getService(String serviceName) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceInfo().getName().equals(serviceName)) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the name = \"" + serviceName + "\"");
    }

    public static StreamingService getServiceByUrl(final String url) throws ExtractionException {
        for (final StreamingService service : ServiceList.all()) {
            if (service.getLinkTypeByUrl(url) != StreamingService.LinkType.NONE) {
                return service;
            }
        }
        throw new ExtractionException("No service can handle the url = \"" + url + "\"");
    }

    public static int getIdOfService(final String serviceName) {
        try {
            return getService(serviceName).getServiceId();
        } catch (final ExtractionException ignored) {
            return -1;
        }
    }

    public static String getNameOfService(final int id) {
        try {
            return getService(id).getServiceInfo().getName();
        } catch (final Exception e) {
            System.err.println("Service id not known");
            e.printStackTrace();
            return "<unknown>";
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Localization
    //////////////////////////////////////////////////////////////////////////*/

    public static void setupLocalization(final Localization thePreferredLocalization) {
        setupLocalization(thePreferredLocalization, null);
    }

    public static void setupLocalization(
            final Localization thePreferredLocalization,
            @Nullable final ContentCountry thePreferredContentCountry) {
        NewPipe.preferredLocalization = thePreferredLocalization;

        if (thePreferredContentCountry != null) {
            NewPipe.preferredContentCountry = thePreferredContentCountry;
        } else {
            NewPipe.preferredContentCountry = thePreferredLocalization.getCountryCode().isEmpty()
                    ? ContentCountry.DEFAULT
                    : new ContentCountry(thePreferredLocalization.getCountryCode());
        }
    }

    @Nonnull
    public static Localization getPreferredLocalization() {
        return preferredLocalization == null ? Localization.DEFAULT : preferredLocalization;
    }

    public static void setPreferredLocalization(final Localization preferredLocalization) {
        NewPipe.preferredLocalization = preferredLocalization;
    }

    @Nonnull
    public static ContentCountry getPreferredContentCountry() {
        return preferredContentCountry == null ? ContentCountry.DEFAULT : preferredContentCountry;
    }

    public static void setPreferredContentCountry(final ContentCountry preferredContentCountry) {
        NewPipe.preferredContentCountry = preferredContentCountry;
    }
}
