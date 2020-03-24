package main;

import com.gasis.di.ObjectManager;
import com.gasis.di.ObjectManagerInterface;
import com.gasis.di.annotation.Parameters;
import com.gasis.di.registry.AbstractArgumentRegistry;
import com.gasis.di.registry.AbstractPreferenceRegistry;

import java.util.Date;

public class Test {

    public static void main(String[] args) {
        ObjectManagerInterface objectManager = new ObjectManager(new PreferenceRegistry(), new ArgumentRegistry());

        InterfaceC c = (InterfaceC) objectManager.instantiate(InterfaceC.class);

        c.c();
    }

    public interface InterfaceA {

        void a();
    }

    public interface InterfaceB {

        void b();
    }

    public interface InterfaceC {

        void c();
    }

    public class A implements InterfaceA {

        @Override
        public void a() {
            System.out.println("A");
        }
    }

    public class B implements InterfaceB {

        private Date date;
        private int number;

        @Parameters({"date", "number"})
        public B(Date date, Integer number) {
            this.date = date;
            this.number = number;
        }

        @Override
        public void b() {
            System.out.println("B: " + number + "; " + date.toString());
        }
    }

    public class C implements InterfaceC {

        private InterfaceA a;
        private InterfaceB b;

        public C(InterfaceA a, InterfaceB b) {
            this.a = a;
            this.b = b;
        }

        public void c() {
            a.a();
            b.b();
        }
    }

    private static class PreferenceRegistry extends AbstractPreferenceRegistry {

        @Override
        protected void initialize() {
            registerPreference(InterfaceA.class, A.class);
            registerPreference(InterfaceB.class, B.class);
            registerPreference(InterfaceC.class, C.class);
        }
    }

    private static class ArgumentRegistry extends AbstractArgumentRegistry {

        @Override
        protected void initialize() {
            registerArgument(B.class, "number", Integer.class, 5);
        }
    }
}
