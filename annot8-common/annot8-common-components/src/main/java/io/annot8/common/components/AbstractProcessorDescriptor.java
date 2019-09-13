/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.common.components;

import io.annot8.core.components.ProcessorDescriptor;
import io.annot8.core.settings.Settings;

public abstract class AbstractProcessorDescriptor<T extends AbstractProcessor, S extends Settings>
    extends AbstractComponentDescriptor<T, S> implements ProcessorDescriptor<T, S> {}
