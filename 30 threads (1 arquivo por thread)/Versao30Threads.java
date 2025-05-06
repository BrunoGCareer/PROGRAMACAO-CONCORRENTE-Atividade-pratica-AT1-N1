import java.io.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Versao30Threads {
    public static void main(String[] args) {
        System.out.println("Iniciando validação com 30 threads (1 arquivo por thread)...");
        
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
        
        // Array para armazenar as 30 threads
        Thread[] threads = new Thread[arquivos.length];
        
        // Criar e iniciar uma thread para cada arquivo
        for (int i = 0; i < arquivos.length; i++) {
            File[] singleFileArray = new File[]{arquivos[i]};
            threads[i] = new Thread(new FileProcessor(singleFileArray, validos, invalidos));
            threads[i].start();
        }
        
        // Esperar todas as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        long fim = System.currentTimeMillis();
        long tempoTotal = fim - inicio;
        
        System.out.println("\nRESULTADOS FINAIS:");
        System.out.println("CPFs válidos: " + validos.get());
        System.out.println("CPFs inválidos: " + invalidos.get());
        System.out.println("Tempo total: " + tempoTotal + " milissegundos");
        
        try (PrintWriter writer = new PrintWriter("versao_30_threads.txt")) {
            writer.println("Tempo de execução: " + tempoTotal + " ms");
            writer.println("CPFs válidos: " + validos.get());
            writer.println("CPFs inválidos: " + invalidos.get());
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar resultados finais");
            e.printStackTrace();
        }
    }
}