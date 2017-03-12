import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.skynet.pipeline.PipelineEngine;
import com.skynet.pipeline.PipelineException;

public class PipelineTest {

    public static void main(final String[] args) {
        final ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-all.xml");
        final PipelineEngine pipelineEngine = context.getBean("pipelineEngine",
                PipelineEngine.class);
        try {
            pipelineEngine.executePipeline("registerBiz", null);
        } catch (final PipelineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
