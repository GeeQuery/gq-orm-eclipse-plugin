package jef.ui.swt;

import org.eclipse.jface.resource.ImageDescriptor;

public interface ImgProvider extends Provider {
	ImageDescriptor getImageDesc(Object obj);
}
