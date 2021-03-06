# Dependency injection container
## Usage Notes
* Works for any class that can be accessed (including inner classes) and has registered or instantiable parameters
* If any constructor arguments are registered, the constructor MUST be annotated with @Parameters annotation listing all parameter names, OR the registered argument names (not constructor parameter names) MUST be 'arg0', 'arg1', 'arg2'...
* If stack overflow error occurs, your classes are bad, because they depend on each other and cannot be created without one another
* If you need primitive types in a constructor, use primitive type wrapper types instead, e.g. Integer instead of int
* Preferences are registered in a class that extends AbstractPreferenceRegistry inside initialize() method, or by implementing PreferenceRegistryInterface
* Arguments are registered in a class that extends AbstractArgumentRegistry inside initialize() method, or by implementing ArgumentRegistryInterface
* If a class has a no-parameter constructor it will be used instead of other constructors
* Registering preferences for concrete types is not necessary - concrete types will automatically be instantiated given they have a constructor that does not require registered arguments and constructor types can be instantiated
* If no arguments are being registered, create ObjectManager like this: new ObjectManager(<preference_registry>, null)
* If a constructor accepts a non-primitive type as an argument, type preferences can be registered as arguments for that constructor
## Example Usage

```java
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
    
    public interface InterfaceD {
    
        void d();
    }

    public class A implements InterfaceA {

        private Date date;
        
        public A(Date date) {
            this.date = date;
        }

        @Override
        public void a() {
            System.out.println("A: " + date.toString());
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
        private InterfaceD d;

        @Parameters({"a", "b", "d"})
        public C(InterfaceA a, InterfaceB b, InterfaceD d) {
            this.a = a;
            this.b = b;
            this.d = d;
        }

        @Override
        public void c() {
            a.a();
            b.b();
            d.d();
        }
    }
    
    public class D implements InterfaceD {
    
        @Override
        public void d() {
            System.out.println("D");
        }
    }

    public class PreferenceRegistry extends AbstractPreferenceRegistry {

        @Override
        protected void initialize() {
            registerPreference(InterfaceA.class, A.class);
            registerPreference(InterfaceB.class, B.class);
            registerPreference(InterfaceC.class, C.class);
        }
    }

    public class ArgumentRegistry extends AbstractArgumentRegistry {

        @Override
        protected void initialize() {
            registerArgument(B.class, "number", Integer.class, 5);
            registerArgument(C.class, "d", Class.class, D.class);
        }
    }
```
