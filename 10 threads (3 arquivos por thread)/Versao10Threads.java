import java.io.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Versao10Threads {
    public static void main(String[] args) {
        System.out.println("Iniciando validação com 10 threads...");
        
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
        
        // Configuração para 10 threads
        int numThreads = 10;
        int filesPerThread = arquivos.length / numThreads;
        int remainingFiles = arquivos.length % numThreads;
        
        // Array para armazenar as threads
        Thread[] threads = new Thread[numThreads];
        int currentFileIndex = 0;
        
        // Criar e iniciar as threads
        for (int i = 0; i < numThreads; i++) {
            // Calcula quantos arquivos esta thread vai processar
            int filesToProcess = filesPerThread + (i < remainingFiles ? 1 : 0);
            File[] filesForThread = new File[filesToProcess];
            
            // Copia os arquivos para esta thread
            System.arraycopy(arquivos, currentFileIndex, filesForThread, 0, filesToProcess);
            currentFileIndex += filesToProcess;
            
            // Cria e inicia a thread
            threads[i] = new Thread(new FileProcessor(filesForThread, validos, invalidos));
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
        
        System.out.println("\nRESULTADOS:");
        System.out.println("CPFs válidos: " + validos.get());
        System.out.println("CPFs inválidos: " + invalidos.get());
        System.out.println("Tempo total: " + tempoTotal + " milissegundos");
        
        try (PrintWriter writer = new PrintWriter("versao_10_threads.txt")) {
            writer.println("Tempo de execução: " + tempoTotal + " ms");
            writer.println("CPFs válidos: " + validos.get());
            writer.println("CPFs inválidos: " + invalidos.get());
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar resultados");
            e.printStackTrace();
        }
    }
}