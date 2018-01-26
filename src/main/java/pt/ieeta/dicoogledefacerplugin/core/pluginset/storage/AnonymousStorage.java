///*  Copyright   2016 - Jorge Miguel Ferreira da Silva
// *
// *  This file is part of AnonymousPatientData.
// *
// *  AnonymousPluginSet is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
// *
// *  AnonymousPluginSet is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with PACScloud.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package pt.ieeta.dicoogledefacerplugin.core.pluginset.storage;
//
///**
// * @author Jorge Miguel Ferreira da Silva
// *
// */
//
//import org.dcm4che2.data.DicomObject;
//import org.dcm4che2.io.DicomInputStream;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import pt.ieeta.dicoogledefacerplugin.core.AnonimizeDicomObject;
//import pt.ua.dicoogle.sdk.StorageInputStream;
//import pt.ua.dicoogle.sdk.StorageInterface;
//import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
//import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;
//import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URI;
//import java.util.Objects;
//
//public class AnonymousStorage implements StorageInterface, PlatformCommunicatorInterface{
//
//	private static final Logger logger = LoggerFactory.getLogger(AnonymousStorage.class);
//	private ConfigurationHolder settings;
//	private boolean enabled=true;
//	private String AnonymousScheme ="defacerScheme";
//	protected DicooglePlatformInterface platform;
//	private String scheme;
//	public AnonymousStorage() {
//		super();
//		logger.info("Initializing -> Anonymous Storage");
//	}
//
//	@Override
//	public void setPlatformProxy(DicooglePlatformInterface core) {
//		this.platform=core;
//	}
//
//	@Override
//	public URI store(DicomObject dcmObj, Object... arg1) {
//		if (!enabled || dcmObj == null) {return null;}
//		try {
////			AnonimizeDicomObject.anonymizeObject(dcmObj);
//		} catch ( IOException e) {
//			logger.warn("Issue while using Anonymous Storage",e);
//		}
//		return this.platform.getStoragePluginForSchema(this.scheme).store(dcmObj, arg1);
//	}
//
//	@Override
//	public URI store(DicomInputStream arg0, Object... arg1) throws IOException {
//		if (!enabled || arg0 == null) {
//			return null;
//		}
//		DicomObject obj = arg0.readDicomObject();
//		URI uri = store(obj);
//		logger.info("Stored at {}", uri);
//		return uri;
//	}
//
//
//	@Override
//	public void setSettings(ConfigurationHolder arg0) {
//		this.scheme=arg0.getConfiguration().getString("scheme","file");
//		this.settings=arg0;
//	}
//
//	@Override
//	public String getScheme() {
//		return this.AnonymousScheme ;
//	}
//
//	@Override
//	public boolean handles(URI location) {
//		return Objects.equals(getScheme(), location.getScheme());
//	}
//
//	@Override
//	public Iterable<StorageInputStream> at(final URI location, Object... arg1) {
//		return this.platform.getStorageForSchema(this.scheme).at(location, arg1);
//	}
//
//
//
//	@Override
//	public boolean disable() {
//		return this.enabled=false;
//	}
//
//	@Override
//	public boolean enable() {
//		return this.enabled=true;
//	}
//
//	@Override
//	public boolean isEnabled() {
//		return this.enabled;
//	}
//
//	@Override
//	public String getName() {
//		return "Anonymous-Wrapper-Plugin";
//	}
//
//	@Override
//	public ConfigurationHolder getSettings() {
//		return this.settings;
//	}
//
//	@Override
//	public void remove(URI arg0) {
//		if(!arg0.getScheme().equals(AnonymousScheme)){
//			return;
//		}
//
//		String path= arg0.getSchemeSpecificPart();
//		File file = new File(path);
//		if (file.exists()){
//			file.delete();
//			System.out.println("Remove done");
//		}
//	}
//
//
//
//
//}
//
//
//
