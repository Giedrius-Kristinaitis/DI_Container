# Dependency injection container
## Usage Notes
* Works for any classes that can be accessed (including inner classes)
* If any constructor arguments are registered, the constructor MUST be annotated with @Parameters annotation listing all parameter names
* If stack overflow error occurs, your classes are bad, because they depend on each other and cannot be created without one another
* If you need primitive types in a constructor, use primitive type wrapper types instead, e.g. Integer instead of int
* Preferences are registered in a class that extends AbstractPreferenceRegistry inside initialize() method
* Arguments are registered in a class that extends AbstractArgumentRegistry inside initialize() method
* If a class has a no-parameter constructor it will be used instead of other constructors
* Registering preferences for concrete types is not necessary - concrete types will automatically be instantiated given they have a constructor that does not require registered arguments and constructor types can be instantiated
* If no arguments are being registered, create ObjectManager like this: new ObjectManager(<preference_registry>, null)
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
        }
    }
```
