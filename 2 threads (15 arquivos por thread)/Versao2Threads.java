import java.io.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Versao2Threads {
    public static void main(String[] args) {
        System.out.println("Iniciando validação com 2 threads...");
        
        String pastaArquivos = "cpfs";
        AtomicInteger validos = new AtomicInteger(0);
        AtomicInteger invalidos = new AtomicInteger(0);
        long inicio = System.currentTimeMillis();
        
        File pasta = new File(pastaArquivos);
        File[] arquivos = pasta.listFiles();
        
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo encontrado na pasta: " + pastaArquivos);
            return;
        }
        
        System.out.println("Encontrados " + arquivos.length + " arquivos para processar");
        
        // Dividir os arquivos para 2 threads
        int metade = arquivos.length / 2;
        File[] arquivosThread1 = new File[metade];
        File[] arquivosThread2 = new File[arquivos.length - metade];
        
        System.arraycopy(arquivos, 0, arquivosThread1, 0, metade);
        System.arraycopy(arquivos, metade, arquivosThread2, 0, arquivos.length - metade);
        
        // Criar as threads
        Thread thread1 = new Thread(new FileProcessor(arquivosThread1, validos, invalidos));
        Thread thread2 = new Thread(new FileProcessor(arquivosThread2, validos, invalidos));
        
        // Iniciar as threads
        thread1.start();
        thread2.start();
        
        // Esperar threads terminarem
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long fim = System.currentTimeMillis();
        long tempoTotal = fim - inicio;
        
        System.out.println("\nRESULTADOS:");
        System.out.println("CPFs válidos: " + validos.get());
        System.out.println("CPFs inválidos: " + invalidos.get());
        System.out.println("Tempo total: " + tempoTotal + " milissegundos");
        
        try (PrintWriter writer = new PrintWriter("versao_2_threads.txt")) {
            writer.println("Tempo de execução: " + tempoTotal + " ms");
            writer.println("CPFs válidos: " + validos.get());
            writer.println("CPFs inválidos: " + invalidos.get());
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar resultados");
            e.printStackTrace();
        }
    }
}