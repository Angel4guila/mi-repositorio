package ArbolB;
import java.util.Scanner;
import java.util.Arrays;

class nodoArbol {
    int[] llaves;
    int m; // Máximo de hijos por nodo
    nodoArbol[] hijos;
    int n; // Número actual de claves
    boolean izquierda;

    public nodoArbol(int m, boolean izquierda) {
        this.m = m;
        this.izquierda = izquierda;
        this.llaves = new int[m - 1];
        this.hijos = new nodoArbol[m];
        this.n = 0;
    }

    public int raiz(int k) {
        int idx = 0;
        while (idx < n && llaves[idx] < k)
            ++idx;
        return idx;
    }

    public void eliminar(int k) {
        int idx = raiz(k);

        if (idx < n && llaves[idx] == k) {
            if (izquierda)
                eliminarFromizquierda(idx);
            else
                eliminarFromNonizquierda(idx);
        } else {
            if (izquierda) {
                System.out.println("La clave " + k + " no existe en el árbol");
                return;
            }

            boolean flag = (idx == n);

            if (hijos[idx].n < (m - 1) / 2)
                llenar(idx);

            if (flag && idx > n)
                hijos[idx - 1].eliminar(k);
            else
                hijos[idx].eliminar(k);
        }
    }

    private void eliminarFromizquierda(int idx) {
        for (int i = idx + 1; i < n; ++i)
            llaves[i - 1] = llaves[i];
        n--;
    }

    private void eliminarFromNonizquierda(int idx) {
        int k = llaves[idx];

        if (hijos[idx].n >= (m - 1) / 2 + 1) {
            int pred = getPredesesor(idx);
            llaves[idx] = pred;
            hijos[idx].eliminar(pred);
        } else if (hijos[idx + 1].n >= (m - 1) / 2 + 1) {
            int succ = getSucesor(idx);
            llaves[idx] = succ;
            hijos[idx + 1].eliminar(succ);
        } else {
            fusionarNodos(idx);
            hijos[idx].eliminar(k);
        }
    }

    private int getPredesesor(int idx) {
        nodoArbol aux = hijos[idx];
        while (!aux.izquierda)
            aux = aux.hijos[aux.n];
        return aux.llaves[aux.n - 1];
    }

    private int getSucesor(int idx) {
        nodoArbol aux = hijos[idx + 1];
        while (!aux.izquierda)
            aux = aux.hijos[0];
        return aux.llaves[0];
    }

    private void llenar(int idx) {
        if (idx != 0 && hijos[idx - 1].n >= (m - 1) / 2 + 1)
            claveHIzquierdo(idx);
        else if (idx != n && hijos[idx + 1].n >= (m - 1) / 2 + 1)
            claveHDerecho(idx);
        else {
            if (idx != n)
                fusionarNodos(idx);
            else
                fusionarNodos(idx - 1);
        }
    }

    private void claveHIzquierdo(int idx) {
        nodoArbol hijo = hijos[idx];
        nodoArbol hermano = hijos[idx - 1];

        for (int i = hijo.n - 1; i >= 0; --i)
            hijo.llaves[i + 1] = hijo.llaves[i];

        if (!hijo.izquierda) {
            for (int i = hijo.n; i >= 0; --i)
                hijo.hijos[i + 1] = hijo.hijos[i];
        }

        hijo.llaves[0] = llaves[idx - 1];

        if (!hijo.izquierda)
            hijo.hijos[0] = hermano.hijos[hermano.n];

        llaves[idx - 1] = hermano.llaves[hermano.n - 1];

        hijo.n += 1;
        hermano.n -= 1;
    }

    private void claveHDerecho(int idx) {
        nodoArbol hijo = hijos[idx];
        nodoArbol hermano = hijos[idx + 1];

        hijo.llaves[hijo.n] = llaves[idx];

        if (!hijo.izquierda)
            hijo.hijos[hijo.n + 1] = hermano.hijos[0];

        llaves[idx] = hermano.llaves[0];

        for (int i = 1; i < hermano.n; ++i)
            hermano.llaves[i - 1] = hermano.llaves[i];

        if (!hermano.izquierda) {
            for (int i = 1; i <= hermano.n; ++i)
                hermano.hijos[i - 1] = hermano.hijos[i];
        }

        hijo.n += 1;
        hermano.n -= 1;
    }

    private void fusionarNodos(int idx) {
        nodoArbol hijo = hijos[idx];
        nodoArbol hermano = hijos[idx + 1];

        hijo.llaves[(m - 1) / 2 - 1] = llaves[idx];

        for (int i = 0; i < hermano.n; ++i)
            hijo.llaves[i + (m - 1) / 2] = hermano.llaves[i];

        if (!hijo.izquierda) {
            for (int i = 0; i <= hermano.n; ++i)
                hijo.hijos[i + (m - 1) / 2] = hermano.hijos[i];
        }

        for (int i = idx + 1; i < n; ++i)
            llaves[i - 1] = llaves[i];

        for (int i = idx + 2; i <= n; ++i)
            hijos[i - 1] = hijos[i];

        hijo.n += hermano.n + 1;
        n--;
    }

    public void insertarNodoNoLleno(int k) {
        int i = n - 1;

        if (izquierda) {
            while (i >= 0 && llaves[i] > k) {
                llaves[i + 1] = llaves[i];
                i--;
            }
            llaves[i + 1] = k;
            n++;
        } else {
            while (i >= 0 && llaves[i] > k)
                i--;

            if (hijos[i + 1].n == m - 1) {
                dividirNodo(i + 1, hijos[i + 1]);

                if (llaves[i + 1] < k)
                    i++;
            }
            hijos[i + 1].insertarNodoNoLleno(k);
        }
    }

    public void dividirNodo(int i, nodoArbol y) {
        nodoArbol z = new nodoArbol(m, y.izquierda);
        z.n = (m - 1) / 2;
        
        // Cambio clave: Usar posición fija para la división
        int pivot = (m - 1) / 2;  // posición media exacta
        
        for (int j = 0; j < z.n; j++)
            z.llaves[j] = y.llaves[j + pivot + 1];
        
        if (!y.izquierda) {
            for (int j = 0; j <= z.n; j++)
                z.hijos[j] = y.hijos[j + pivot + 1];
        }
        
        y.n = pivot;  // ajustar tamaño
        
        // Mover hijos del nuevo nodo
        for (int j = n; j >= i + 1; j--)
            hijos[j + 1] = hijos[j];
        
        hijos[i + 1] = z;
        
        // Mover claves
        for (int j = n - 1; j >= i; j--)
            llaves[j + 1] = llaves[j];
        
        llaves[i] = y.llaves[pivot];  // promover el elemento medio exacto
        n++;
    }

    public boolean buscar (int k) {
        int i = 0;
        while (i < n && k > llaves[i])
            i++;

        if (i < n && k == llaves[i])
            return true;

        if (izquierda)
            return false;

        return hijos[i].buscar (k);
    }
}

class BTree {
    nodoArbol root;
    int m; // Máximo de hijos por nodo

    public BTree(int m) {
        this.root = null;
        this.m = m;
    }

    public void insert(int k) {
        if (root == null) {
            root = new nodoArbol(m, true);
            root.llaves[0] = k;
            root.n = 1;
        } else {
            if (root.n == m - 1) {
                nodoArbol s = new nodoArbol(m, false);
                s.hijos[0] = root;
                s.dividirNodo(0, root);

                int i = 0;
                if (s.llaves[0] < k)
                    i++;
                s.hijos[i].insertarNodoNoLleno(k);

                root = s;
            } else {
                root.insertarNodoNoLleno(k);
            }
        }
    }

    public void eliminar(int k) {
        if (root == null) {
            System.out.println("El árbol está vacío");
            return;
        }

        root.eliminar(k);

        if (root.n == 0) {
            if (root.izquierda)
                root = null;
            else
                root = root.hijos[0];
        }
    }

    public void display() {
        if (root != null) {
            System.out.println("Estructura del Árbol B (m=" + m + "):");
            displayReauxsive(root, 0);
        } else {
            System.out.println("El árbol está vacío");
        }
    }

    private void displayReauxsive(nodoArbol node, int level) {
        System.out.print("Nivel " + level + ": ");
        for (int i = 0; i < node.n; i++) {
            System.out.print(node.llaves[i] + " ");
        }
        System.out.println();

        if (!node.izquierda) {
            for (int i = 0; i <= node.n; i++) {
                if (node.hijos[i] != null) {
                    displayReauxsive(node.hijos[i], level + 1);
                }
            }
        }
    }

    public boolean buscar (int k) {
        return (root == null) ? false : root.buscar (k);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BTree bTree = null;

        System.out.println("Bienvenido al Árbol B");
        System.out.print("Ingrese el grado m del árbol: ");
        int m = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (m >= 3) {
            bTree = new BTree(m);
            System.out.println("\nÁrbol B creado con m = " + m);
            System.out.println("Máximo de claves por nodo: " + (m - 1));
            System.out.println("Mínimo de claves por nodo (excepto raíz): " + ((m - 1) / 2));
            System.out.println("Máximo de hijos por nodo: " + m);
            System.out.println("Mínimo de hijos por nodo (excepto raíz): " + (m / 2));
            
            System.out.print("\nIngrese todos los elementos separados por espacios: ");
            String input = scanner.nextLine();
            
            try {
                int[] elementos = Arrays.stream(input.split("\\s+"))
                                      .mapToInt(Integer::parseInt)
                                      .toArray();
                
                System.out.println("\nInsertando elementos...");
                for (int valor : elementos) {
                    bTree.insert(valor);
                    System.out.print(valor + " ");
                }
                System.out.println("\n\nTodos los elementos han sido insertados correctamente.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingrese solo números separados por espacios.");
                System.exit(1);
            }
        } else {
            System.out.println("El valor de m debe ser al menos 3");
            System.exit(1);
        }

        // Menú principal
        while (true) {
            System.out.println("\nMenú del Árbol B");
            System.out.println("1. Insertar elemento");
            System.out.println("2. Eliminar elemento");
            System.out.println("3. Mostrar árbol");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el valor a insertar: ");
                    int valorInsertar = scanner.nextInt();
                    bTree.insert(valorInsertar);
                    System.out.println("Valor " + valorInsertar + " insertado");
                    break;
                case 2:
                    System.out.print("Ingrese el valor a eliminar: ");
                    int valorEliminar = scanner.nextInt();
                    bTree.eliminar(valorEliminar);
                    break;
                case 3:
                    bTree.display();
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
}
