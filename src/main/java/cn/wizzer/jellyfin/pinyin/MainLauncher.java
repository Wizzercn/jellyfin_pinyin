package cn.wizzer.jellyfin.pinyin;

import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init")
public class MainLauncher {
    @Inject("refer:$ioc")
    private Ioc ioc;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("cn.wizzer");
        nb.run();
    }

    public void init() throws Exception {
        ioc.get(JellyfinHandler.class).init();
    }

}
