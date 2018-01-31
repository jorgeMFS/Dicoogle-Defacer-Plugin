/*  Copyright   2016 - Jorge Miguel Ferreira da Silva
 *
 *  This file is part of AnonymousPatientData.
 *
 *  AnonymousPluginSet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AnonymousPluginSet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PACScloud.  If not, see <http://www.gnu.org/licenses/>.
 */


package pt.ieeta.dicoogledefacerplugin.core.pluginset;


import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.restlet.resource.ServerResource;
import pt.ieeta.dicoogledefacerplugin.core.pluginset.jetty.DefacerServletPlugin;
import pt.ua.dicoogle.sdk.*;
import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Jorge Miguel Ferreira da Silva
 */


@SuppressWarnings("deprecation")
@PluginImplementation
public class DefacerPluginSet implements PluginSet, PlatformCommunicatorInterface {

	protected DicooglePlatformInterface platform;
	private ConfigurationHolder settings;


	@Override
	public void setPlatformProxy(DicooglePlatformInterface core) {
		this.platform=core;
	}


	@Override
	public String getName() {
		return "Defacer-Plugin";
	}

	private String location="/default/";
	private DefacerServletPlugin jetty_servlet = new DefacerServletPlugin();


	@Override
	public Collection<StorageInterface> getStoragePlugins() {
        return Collections.emptyList();	}


	@Override
	public Collection<GraphicalInterface> getGraphicalPlugins() {
		return Collections.emptyList();
	}

	@Override
	public Collection<IndexerInterface> getIndexPlugins() {
		return Collections.emptyList();
	}

	@Override
	public Collection<JettyPluginInterface> getJettyPlugins() {
		return Collections.singleton(this.jetty_servlet);
	}

	@Override
	public Collection<QueryInterface> getQueryPlugins() {
        return Collections.emptyList();}

	@Override
	public Collection<ServerResource> getRestPlugins() {
		return Collections.emptyList();
	}

	@Override
	public ConfigurationHolder getSettings() {
		return this.settings;
	}

	@Override
	public void setSettings(ConfigurationHolder arg0) {
		this.setLocation(arg0.getConfiguration().getString("Location","./default/"));
		this.settings =arg0;
	}

	@Override
	public void shutdown() {
	}


	public void setLocation(String location) {
		this.location = location;
	}


}
