package io.annot8.common.pipelines.impl;

import io.annot8.common.implementations.stores.NotifyingItemFactory;
import io.annot8.common.pipelines.PipelineDescriptor;
import io.annot8.core.components.Processor;
import io.annot8.core.components.Source;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryPipelineRunner implements Runnable {

  private final PipelineDescriptor pipelineDescriptor;
  private final ItemFactory itemFactory;

  private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryPipelineRunner.class);

  private boolean running = true;

  public InMemoryPipelineRunner(PipelineDescriptor pipelineDescriptor, ItemFactory itemFactory){
    this.pipelineDescriptor = pipelineDescriptor;
    this.itemFactory = itemFactory;
  }

  @Override
  public void run() {
    List<Item> itemsToProcess = new ArrayList<>();

    NotifyingItemFactory nif = new NotifyingItemFactory(itemFactory);
    nif.register(itemsToProcess::add);
    nif.register(i -> LOGGER.debug("Item {} added to queue", i.getId()));

    List<Source> activeSources = new ArrayList<>();
    pipelineDescriptor.getSources().forEach(sd -> activeSources.add((Source)sd.create()));

    List<Processor> activeProcessors = new ArrayList<>();
    pipelineDescriptor.getProcessors().forEach(pd -> activeProcessors.add((Processor)pd.create()));

    while(running && !activeSources.isEmpty()) {
      Iterator<Source> sourceIter = activeSources.iterator();
      while(sourceIter.hasNext()){
        Source source = sourceIter.next();

        LOGGER.debug("[{}] Reading source {} for new items", pipelineDescriptor.getName(), source.toString());
        SourceResponse response = source.read(nif);

        switch (response.getStatus()) {
          case DONE:
            LOGGER.info("[{}] Finished reading all items from source {}", pipelineDescriptor.getName(), source.toString());
            sourceIter.remove();
            break;
          case SOURCE_ERROR:
            LOGGER.error("[{}] Source {} returned a non-recoverable error and has been removed from the pipeline", pipelineDescriptor.getName(), source.toString());
            if(response.hasExceptions()) {
              for(Exception e : response.getExceptions()){
                LOGGER.error("The following exception was caught by the source", e);
              }
            }

            sourceIter.remove();
            break;
        }

        if(!running)
          break;
      }

      //Process current items
      while(running && !itemsToProcess.isEmpty()){
        Item item = itemsToProcess.remove(0);
        LOGGER.debug("[{}] Beginning processing of item {}", pipelineDescriptor.getName(), item.getId());

        Iterator<Processor> processorIter = activeProcessors.iterator();

        while(processorIter.hasNext()){
          Processor processor = processorIter.next();

          LOGGER.debug("[{}] Processing item {} using processor {}", pipelineDescriptor.getName(), item.getId(), processor.toString());
          ProcessorResponse response = processor.process(item);

          if(response.getStatus() == ProcessorResponse.Status.ITEM_ERROR){
            LOGGER.error("[{}] Processor {} returned an error whilst processing the current item {}, and the item will not be processed by the remainder of the pipeline", pipelineDescriptor.getName(), processor.toString(), item.getId());
            if(response.hasExceptions()) {
              for(Exception e : response.getExceptions()){
                LOGGER.error("The following exception was caught by the processor", e);
              }
            }
            break;
          }else if(response.getStatus() == ProcessorResponse.Status.PROCESSOR_ERROR){
            LOGGER.error("[{}] Processor {} returned a non-recoverable error whilst processing the current item {}, and the processor has been removed from the pipeline", pipelineDescriptor.getName(), processor.toString(), item.getId());
            if(response.hasExceptions()) {
              for(Exception e : response.getExceptions()){
                LOGGER.error("The following exception was caught by the processor", e);
              }
            }

            processorIter.remove();
          }
        }
      }
    }
  }

  public void stop(){
    LOGGER.info("Stopping pipeline after current item/source");
    running = false;
  }
}
