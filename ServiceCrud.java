/* Autores: Samuel Pietra
 * */

import java.util.Scanner;


import java.io.*;

public class ServiceCrud{

	private RandomAccessFile arq;

	/*
	 *Construtor da classe
	 *@param instancia de RAF com o arquivo aberto
	 * */
	public ServiceCrud(RandomAccessFile arq){
		this.arq = arq;
	}//end ServiceCrud()

	/*
	 * Escreve o filme no arquivo
	 * @param uma instancia de filme a ser gravada
	 * @param id do filme a ser gravado
	 * */
	public void create(Filme filme, int id){
		try {
			if(id == -1) {
				if(arq.length() == 0) {
					id = 0;
				} else {
					arq.seek(0);
					id = arq.readInt();
					id++;
				}
			}
			arq.seek(0);
			arq.writeInt(id);
			arq.seek(arq.length());
			filme.setId(id);
			filme.writeObject(arq);
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}//end create()

	/*
	 * Deleta o filme do arquivo(alterando a lapide
	 * @param id do filme a ser deletado
	 * */
	public void delete(int id){
		Scanner input = new Scanner(System.in);
		long pointArq = searchPointer(id);

		if(pointArq !=0){
			try{
				read(pointArq);
				System.out.println("Deseja confirmar a exclusão? Insira (1): ");
				if( input.nextByte() == 1 ){
					arq.seek(pointArq);
					arq.writeChar('*');
				}
				else
					System.out.println("Exclusão cancelada!");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		else
			System.out.println("Filme não encontrado!");
	}//end delete()

	/*
	 * Altera as informacoes do filme selecionado
	 * @param id do filme a ser alterado
	 * */
	public void update(int id){
		Scanner input = new Scanner(System.in);
		long pointArq = searchPointer(id);

		if(pointArq !=0){
			try{
				read(pointArq);
				System.out.println("Deseja confirmar a alteração? Insira (1): ");
				if( input.nextByte() == 1 ){
					arq.seek(pointArq);
					arq.writeChar('*');
					Filme filme = criarObjetoFilme();
					create(filme,id);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		else
			System.out.println("Filme não encontrado!");

	}//end update()

	/*
	 * Sobrecarga no metodo de leitura
	 * @param id do filme pesquisado
	 * Pesquisa por id antes de ler o Filme
	 */
	public void read(int id){
		read( searchPointer(id) );
	}

	/*
	 * Pesquisa as informacoes de um filme no arquivo
	 * @param endereço do ponteiro do filme pesquisado
	 **/
	public void read(long pointerArq){
		
		if(pointerArq != 0){
			try{
				arq.seek(pointerArq);
				arq.skipBytes(2);

				int tam = arq.readShort();

				byte[] registro = new byte[tam];

				for(short i = 0 ; i < tam; i++)
					registro[i] = arq.readByte();

				Filme filme  = new Filme();

				filme.setByteArray(registro);
				System.out.println(filme.toString());

			}catch(IOException e ){
				e.printStackTrace();
			}
		}
		else
			System.out.println("Filme não encontrado!");		
	}//end read()

	/*
	 * Encontra o ponteiro do inicio de um registro
	 * @param id do registro cujo ponteiro eh desejado
	 * @return ponteiro do registro desejado
	 * */
	private long searchPointer(int id){

		long pointArq = 0;
		long tamArquivo;
		boolean continuar = true;

		try{
			tamArquivo = arq.length();

			if(tamArquivo == 0)
				System.out.println("ERRO : Arquivo vazio!");
			else{
				arq.seek(4);
				pointArq = arq.getFilePointer();
				while(continuar & pointArq < tamArquivo){

					char lapide = arq.readChar();

					short tamRegistro = arq.readShort();

					if(lapide != '*' && arq.readInt() == id )
						continuar = false;
					else{
						arq.seek(pointArq);
						arq.skipBytes(tamRegistro+4);
						pointArq = arq.getFilePointer();

					}	
				}
			}	
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return continuar?0:pointArq;

	}//end searchPointer()

	/*
	 * Cria um objeto de filme com as informacoes da entrada
	 * @return uma instancia de Filme criada
	 * */
	public static Filme criarObjetoFilme(){
		Scanner input = new Scanner(System.in);
		String titulo,tituloOriginal,pais,diretor,sinopse;
		short ano;
		short min;

		Filme filme = null;

		System.out.print("Titulo: ");
		titulo = input.nextLine();

		System.out.print("Titulo Original: ");
		tituloOriginal = input.nextLine();

		System.out.print("Pais de origem: ");
		pais = input.nextLine();

		System.out.print("Diretor: ");
		diretor = input.nextLine();

		System.out.print("Sinopse: ");
		sinopse = input.nextLine();

		System.out.print("Ano: ");
		ano = input.nextShort();

		System.out.print("Minutos filme: ");
		min = input.nextShort();

		System.out.print("Insira 1 para confirma inclusão ou 0 para cancelar: ");
		if(input.nextByte() == 1) {
			filme = new Filme(titulo,tituloOriginal,pais,ano,min,diretor,sinopse);
		}	
		return filme; 
	}//end criarObjetoFilme()
}//end ServiceCrud
