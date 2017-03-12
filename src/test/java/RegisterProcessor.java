import java.util.Map;

import com.dmall.pipeline.AbstractPipelineProcessor;
import com.dmall.pipeline.PipelineException;

public class RegisterProcessor extends AbstractPipelineProcessor {

    public static final int SEND_EMAIL = 1;

    @Override
    protected int process(final Map<String, Object> params)
            throws PipelineException {
        System.out.println("注册用户成功");
        return SEND_EMAIL;
    }

}
