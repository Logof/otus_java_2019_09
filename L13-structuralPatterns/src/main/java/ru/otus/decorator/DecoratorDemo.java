package ru.otus.decorator;

/*
 *
 *   Reader---------------                 InputStream
 *     |                  |                   |
 *     |                  |                   |
 *BufferedReader    InputStreamReader  ( FileInputStream )
 *     |
 *     |
 *StringReader
 *
 * new BufferedReader(new InputStreamReader(new FileInputStream("test.txt")))
 *
 * */
public class DecoratorDemo {
  public static void main(String[] args) {
    DataSource ds = new DataSourceImpl();
    printer(ds);

    printer(new DataSourceDecoratorAdder(ds));
    printer(new DataSourceDecoratorMultiplicator(ds));
    printer(new DataSourceDecoratorAdder(new DataSourceDecoratorMultiplicator(ds)));
  }

  private static void printer(DataSource ds) {
    System.out.println(ds.getInteger());
  }
}
