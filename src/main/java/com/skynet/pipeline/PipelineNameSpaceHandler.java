package com.skynet.pipeline;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class PipelineNameSpaceHandler extends NamespaceHandlerSupport {
    private static class PipelineBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            final String id = element.getAttribute("id");
            if (StringUtils.hasText(id)) {
                builder.addPropertyValue("id", id);
            }
            final String headProcessor = element.getAttribute("headProcessor");
            if (StringUtils.hasText(headProcessor)) {
                builder.addPropertyReference("headProcessor", headProcessor);
            }
            final String isolation = element.getAttribute("isolation");
            if (StringUtils.hasText(isolation)) {
                builder.addPropertyValue("isolation", Isolation.valueOf(isolation));
            }
            final String propagation = element.getAttribute("propagation");
            if (StringUtils.hasText(propagation)) {
                builder.addPropertyValue("propagation", Propagation.valueOf(propagation));
            }
            final List<Element> processorElements = DomUtils.getChildElements(element);
            if (CollectionUtils.isEmpty(processorElements)) {
                return;
            }
            final ManagedMap<String, Object> processors = new ManagedMap<String, Object>();
            processors.setMergeEnabled(true);
            processors.setSource(parserContext.getReaderContext().extractSource(element));
            for (final Element processorElement : processorElements) {
                final String processorId = processorElement.getAttribute("id");
                processors.put(processorId, parserContext.getDelegate().parseCustomElement(processorElement));
            }
            builder.addPropertyValue("processors", processors);
        }

        @Override
        protected Class<Pipeline> getBeanClass(final Element element) {
            return Pipeline.class;
        }

    }

    private static class ProcessorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            final String id = element.getAttribute("id");
            if (StringUtils.hasText(id)) {
                builder.addPropertyValue("id", id);
            }
            final String isolation = element.getAttribute("isolation");
            if (StringUtils.hasText(isolation)) {
                builder.addPropertyValue("isolation", Isolation.valueOf(isolation));
            } else {
                builder.addPropertyValue("isolation", Isolation.DEFAULT);
            }
            final String propagation = element.getAttribute("propagation");
            if (StringUtils.hasText(propagation)) {
                builder.addPropertyValue("propagation", Propagation.valueOf(propagation));
            } else {
                builder.addPropertyValue("propagation", Propagation.REQUIRED);
            }
            final String ref = element.getAttribute("ref");
            builder.addPropertyReference("beanRef", ref);
            final List<Element> resultElements = DomUtils.getChildElementsByTagName(element, "result");
            if (CollectionUtils.isEmpty(resultElements)) {
                return;
            }

            final ManagedMap<String, Object> results = new ManagedMap<String, Object>();
            results.setMergeEnabled(true);
            results.setSource(parserContext.getReaderContext().extractSource(element));
            for (final Element resultElement : resultElements) {
                final String value = resultElement.getAttribute("value");
                final RuntimeBeanReference nextRef = new RuntimeBeanReference(resultElement.getAttribute("nextProcessor"), false);
                nextRef.setSource(parserContext.getReaderContext().extractSource(element));
                results.put(value, nextRef);
            }
            builder.addPropertyValue("results", results);
        }

        @Override
        protected Class<Processor> getBeanClass(final Element element) {
            return Processor.class;
        }
    }

    public void init() {
        this.registerBeanDefinitionParser("pipeline", new PipelineBeanDefinitionParser());
        this.registerBeanDefinitionParser("processor", new ProcessorBeanDefinitionParser());
    }

}
