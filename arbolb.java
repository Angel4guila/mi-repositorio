package ArbolBB;
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
        this.llaves = new int[m - 1]; // Máximo de m-1 claves
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

    // Método insertarNodoNoLleno revisado para comportarse como en la prueba de escritorio
    public void insertarNodoNoLleno(int k) {
        int i = n - 1;

        // Si es una hoja, insertamos directamente
        if (izquierda) {
            // Movemos todas las claves mayores a la derecha
            while (i >= 0 && llaves[i] > k) {
                llaves[i + 1] = llaves[i];
                i--;
            }
            // Insertamos la nueva clave en su posición ordenada
            llaves[i + 1] = k;
            n++;
        } else {
            // Si es un nodo interno, buscamos el hijo adecuado
            while (i >= 0 && llaves[i] > k) {
                i--;
            }
            i++;
            
            // Si el hijo está lleno, lo dividimos antes de insertar
            if (hijos[i].n == m - 1) {
                dividirNodo(i, hijos[i]);
                // Después de dividir, la clave mediana está en este nodo
                // Así que necesitamos determinar qué hijo recibirá k
                if (k > llaves[i]) {
                    i++;
                }
            }
            
            // Insertamos en el hijo adecuado
            hijos[i].insertarNodoNoLleno(k);
        }
    }

public void dividirNodo(int i, nodoArbol y) {
    int t = (m % 2 == 0) ? (m / 2 - 1) : ((m - 1) / 2);  // Manejo para m par e impar
    nodoArbol z = new nodoArbol(m, y.izquierda);
    z.n = y.n - t - 1;  // Número de claves que van a z

    // Copiar claves mayores al nuevo nodo z
    for (int j = 0; j < z.n; j++) {
        z.llaves[j] = y.llaves[j + t + 1];
    }

    // Si no es hoja, copiar los hijos correspondientes
    if (!y.izquierda) {
        for (int j = 0; j <= z.n; j++) {
            z.hijos[j] = y.hijos[j + t + 1];
        }
    }

    y.n = t;  // Actualizar número de claves en y

    // Hacer espacio en el padre para la nueva clave e hijo
    for (int j = n; j > i; j--) {
        hijos[j + 1] = hijos[j];
    }
    hijos[i + 1] = z;

    for (int j = n - 1; j >= i; j--) {
        llaves[j + 1] = llaves[j];
    }
    llaves[i] = y.llaves[t];  // Clave mediana sube al padre
    n++;
}

    public boolean buscar(int k) {
        int i = 0;
        while (i < n && k > llaves[i])
            i++;

        if (i < n && k == llaves[i])
            return true;

        if (izquierda)
            return false;

        return hijos[i].buscar(k);
    }
}

class BTree {
    nodoArbol root;
    int m; // Máximo de hijos por nodo

    public BTree(int m) {
        this.root = null;
        this.m = m;
    }

    // Método insert revisado para que las divisiones resulten en la estructura esperada
    public void insert(int k) {
        // Si el árbol está vacío
        if (root == null) {
            root = new nodoArbol(m, true);
            root.llaves[0] = k;
            root.n = 1;
            return;
        }
        
        // Si la raíz está llena, necesitamos dividirla
        if (root.n == m - 1) {
            // Creamos una nueva raíz
            nodoArbol s = new nodoArbol(m, false);
            // La antigua raíz se convierte en el primer hijo
            s.hijos[0] = root;
            // Dividimos la antigua raíz
            s.dividirNodo(0, root);
            
            // Determinamos en qué hijo insertar la nueva clave
            int i = 0;
            if (s.llaves[0] < k) {
                i++;
            }
            // Insertamos en el hijo apropiado
            s.hijos[i].insertarNodoNoLleno(k);
            
            // La nueva raíz es s
            root = s;
        } else {
            // Si la raíz no está llena, insertamos normalmente
            root.insertarNodoNoLleno(k);
        }
    }

    public boolean verificarConsistencia() {
        return root == null || verificarNodo(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean verificarNodo(nodoArbol nodo, int min, int max) {
        // Verificar claves en orden y en rango
        for (int i = 0; i < nodo.n; i++) {
            if (nodo.llaves[i] <= min || nodo.llaves[i] >= max)
                return false;
        }
        
        // Verificar hijos recursivamente
        if (!nodo.izquierda) {
            for (int i = 0; i <= nodo.n; i++) {
                int nuevoMin = (i == 0) ? min : nodo.llaves[i-1];
                int nuevoMax = (i == nodo.n) ? max : nodo.llaves[i];
                if (!verificarNodo(nodo.hijos[i], nuevoMin, nuevoMax))
                    return false;
            }
        }
        return true;
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

    // Mejorado para mostrar mejor la estructura del árbol
    public void display() {
        if (root != null) {
            System.out.println("Estructura del Árbol B (m=" + m + "):");
            displayRecursive(root, 0);
        } else {
            System.out.println("El árbol está vacío");
        }
    }

        private void displayRecursive(nodoArbol nodo, int nivel) {
        System.out.print("Nivel " + nivel + " [");
        for (int i = 0; i < nodo.n; i++) {
            System.out.print(nodo.llaves[i]);
            if (i != nodo.n - 1) System.out.print(", ");
        }
        System.out.println("]");

        if (!nodo.izquierda) {
            for (int i = 0; i <= nodo.n; i++) {
                displayRecursive(nodo.hijos[i], nivel + 1);
            }
        }
    }


    public boolean buscar(int k) {
        return (root == null) ? false : root.buscar(k);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BTree bTree = null;

        System.out.println("Bienvenido al Árbol B");
        System.out.print("Ingrese el grado m del árbol : ");
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

            // Menú interactivo
            int opcion;
            do {
                System.out.println("\n--- MENÚ ---");
                System.out.println("1. Insertar un elemento");
                System.out.println("2. Eliminar un elemento");
                System.out.println("3. Buscar un elemento");
                System.out.println("4. Mostrar árbol");
                System.out.println("5. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese el valor a insertar: ");
                        int insertar = scanner.nextInt();
                        bTree.insert(insertar);
                        System.out.println("Elemento insertado.");
                        break;
                    case 2:
                        System.out.print("Ingrese el valor a eliminar: ");
                        int eliminar = scanner.nextInt();
                        bTree.eliminar(eliminar);
                        break;
                    case 3:
                        System.out.print("Ingrese el valor a buscar: ");
                        int buscar = scanner.nextInt();
                        boolean encontrado = bTree.buscar(buscar);
                        System.out.println("Resultado de la búsqueda: " + (encontrado ? "Encontrado" : "No encontrado"));
                        break;
                    case 4:
                        bTree.display();
                        break;
                    case 5:
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                }
            } while (opcion != 5);

        } else {
            System.out.println("El grado del árbol B debe ser al menos 3.");
        }

        scanner.close();
    }
}
