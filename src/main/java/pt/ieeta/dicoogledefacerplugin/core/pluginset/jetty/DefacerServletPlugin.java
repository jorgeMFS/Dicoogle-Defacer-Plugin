/*  Copyright   2016 - Jorge Miguel Ferreira da Silva
 *
 *  This file is part of AnonymousPatientData.
 *
 *  AnonymousQuery is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AnonymousQuery is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PACScloud.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ieeta.dicoogledefacerplugin.core.pluginset.jetty;

import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import pt.ua.dicoogle.sdk.JettyPluginInterface;
import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;

import javax.servlet.MultipartConfigElement;

/**
 * @author Jorge Miguel Ferreira da Silva
 */
public class DefacerServletPlugin implements JettyPluginInterface, PlatformCommunicatorInterface {

    private static final String ANON_FILE_PATH = System.getProperty("java.io.tmpdir");
    private final DefacerWebServlet defaceWS;
    private boolean enabled;
    private ConfigurationHolder settings;
    private DicooglePlatformInterface platform;

    public DefacerServletPlugin() {
        this.defaceWS = new DefacerWebServlet();
        this.enabled = true;
    }

    @Override
    public void setPlatformProxy(DicooglePlatformInterface pi) {
        this.setPlatform(pi);
        // since web service is not a plugin interface, the platform interface must be provided manually
        this.defaceWS.setPlatformProxy(pi);
    }

    @Override
    public String getName() {
        return "Anonymous-Wrapper-Plugin";
    }

    @Override
    public boolean enable() {
        this.enabled = true;
        return true;
    }

    @Override
    public boolean disable() {
        this.enabled = false;
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public ConfigurationHolder getSettings() {
        return settings;
    }

    @Override
    public void setSettings(ConfigurationHolder settings) {
        this.settings = settings;
        // use settings here

    }

    @Override
    public HandlerList getJettyHandlers() {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/Anon");
        ServletHolder convertServletHolder = new ServletHolder(this.defaceWS);
        convertServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(ANON_FILE_PATH));
        handler.addServlet(convertServletHolder, "/Store");
        HandlerList l = new HandlerList();
        l.addHandler(handler);
        return l;
    }

    public DicooglePlatformInterface getPlatform() {
        return platform;
    }

    public void setPlatform(DicooglePlatformInterface platform) {
        this.platform = platform;
    }


}