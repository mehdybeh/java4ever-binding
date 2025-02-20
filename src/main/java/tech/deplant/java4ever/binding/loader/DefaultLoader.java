package tech.deplant.java4ever.binding.loader;

import tech.deplant.java4ever.utils.SystemContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.System.Logger.Level.DEBUG;

public record DefaultLoader(ClassLoader loader) implements LibraryLoader {

	private final static System.Logger logger = System.getLogger(DefaultLoader.class.getName());

	public static String resourceToAbsolute(ClassLoader loader, String resourcePath) throws IOException {
		URL url = loader.getResource(resourcePath);

		File lib = null;
		if (url.getProtocol().toLowerCase().equals("file")) {
			try {
				lib = new File(new URI(url.toString()));
			} catch (URISyntaxException e) {
				lib = new File(url.getPath());
			}
			final File finalLib = lib;
			logger.log(DEBUG, () -> "Looking in %s".formatted(finalLib.getAbsolutePath()));
			if (!lib.exists()) {
				throw new IOException("File URL " + url + " could not be properly decoded");
			}
		}
		return lib.getAbsolutePath();
	}

	public void loadJarDll(String name) {
		try {
//			Module module = ModuleLayer.boot()
//			                           .findModule("java4ever.binding")
//			                           // Optional<Module> at this point
//			                           .orElseThrow();
			//InputStream in = module.getResourceAsStream(name);
			InputStream in = this.loader().getResourceAsStream(name);
			//InputStream in = this.getClass().getResourceAsStream(name);
			byte[] buffer = new byte[1024];
			int read = -1;
			File temp = null;
			temp = File.createTempFile(name, "");
			FileOutputStream fos = new FileOutputStream(temp);

			while ((read = in.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.close();
			in.close();

			System.load(temp.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void load() {
		if (SystemContext.OS().equals(SystemContext.OperatingSystem.WINDOWS) &&
		    SystemContext.PROCESSOR().equals(SystemContext.ProcessorArchitecture.X86_64)) {
			loadJarDll("sdk/win_x86_64/ton_client.dll");
		} else if (SystemContext.OS().equals(SystemContext.OperatingSystem.LINUX) &&
		           SystemContext.PROCESSOR().equals(SystemContext.ProcessorArchitecture.X86_64)) {
			loadJarDll("sdk/linux_x86_64/libton_client.so");
		} else if (SystemContext.OS().equals(SystemContext.OperatingSystem.MAC) &&
		           SystemContext.PROCESSOR().equals(SystemContext.ProcessorArchitecture.X86_64)) {
			loadJarDll("sdk/macos_x86_64/libton_client.dylib");
		} else if (SystemContext.OS().equals(SystemContext.OperatingSystem.MAC) &&
		           SystemContext.PROCESSOR().equals(SystemContext.ProcessorArchitecture.ARM_64)) {
			loadJarDll("sdk/macos_aarch64/libton_client.dylib");
		} else {
			throw new RuntimeException("Unsupported architecture, use other loaders for your custom EVER-SDK library!");
		}

	}

}
