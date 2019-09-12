package io.annot8.common.serialization;

import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;

public class TestProcessor implements Processor {

  public TestProcessor(TestSettings settings){
    //E.g. connect to host and port
  }

  @Override
  public ProcessorResponse process(Item item) {
    return ProcessorResponse.ok();
  }
}