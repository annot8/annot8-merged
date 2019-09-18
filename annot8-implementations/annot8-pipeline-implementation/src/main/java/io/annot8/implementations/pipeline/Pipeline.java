package io.annot8.implementations.pipeline;

import io.annot8.api.components.Processor;
import io.annot8.api.components.Resource;
import io.annot8.api.components.Source;
import io.annot8.api.components.responses.ProcessorResponse;
import io.annot8.api.components.responses.SourceResponse;
import io.annot8.api.context.Context;
import io.annot8.api.data.Item;
import io.annot8.api.data.ItemFactory;
import io.annot8.api.pipelines.PipelineDescriptor;

import java.io.Closeable;
import java.util.Collection;

public interface Pipeline extends Closeable {

  String getName();

  String getDescription();

  Context getContext();

  Collection<Source> getSources();

  Collection<Processor> getProcessors();

  SourceResponse read(ItemFactory itemFactory);

  ProcessorResponse process(Item item);

  interface Builder {

    Builder from(PipelineDescriptor pipelineDescriptor);

    Builder withName(String name);

    Builder withDescription(String description) ;

    Builder withSource(Source source);

    Builder withProcessor(Processor processor);

    Builder withResource(Resource resource);

    Pipeline build();

  }
}
