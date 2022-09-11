package com.platzi.gatos_app;

import com.google.gson.Gson;
import com.squareup.okhttp.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GatosService {

    public static void verGatos() throws IOException {
        // vamos a traer los datos de la API
        OkHttpClient client = new OkHttpClient();
        //.newBuilder()
        //                .build()
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String elJson = response.body().string();

        //cortar los corchetes
        elJson = elJson.substring(1,elJson.length());
        elJson = elJson.substring(0,elJson.length()-1);

        //crear un objeto de la clase Gson
        Gson gson = new Gson();
        Gatos gatos = gson.fromJson(elJson, Gatos.class);

        //redimensionar en caso de necesitar
        Image image = null;
        try {
            URL url = new URL(gatos.getUrl());

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.addRequestProperty("User-Agent", "");
            BufferedImage bufferedImage = ImageIO.read(http.getInputStream());

            //image = ImageIO.read(url);

            ImageIcon fondoGato = new ImageIcon(bufferedImage);
            if(fondoGato.getIconWidth()>800){
                //redimensionamos
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                fondoGato = new ImageIcon(modificada);
            }
            String menu = "Opciones: \n"+
                    " 1. ver otra imagen \n"+
                    " 2. favorito \n"+
                    " 3. volver \n";
            String[] botones = {"ver otra imagen","favorito","volver"};
            String id_gato = gatos.getId();
            String opcion = (String) JOptionPane.showInputDialog(null,menu,id_gato,
                    JOptionPane.INFORMATION_MESSAGE,fondoGato,
                    botones, botones[0]);

            int seleccion = -1;
            for (int i = 0; i < botones.length; i++) {
                if(opcion.equals(botones[i])){
                    seleccion = i;
                }
            }
            switch (seleccion){
                case 0:
                    verGatos();
                    break;
                case 1:
                    favoritoGato(gatos);
                    break;
                default:
                    break;

            }

        }catch (IOException e){
            System.out.println(e);
        }

    };

    public static void favoritoGato(Gatos gato){
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"image_id\":\""+gato.getId()+"\"\n}");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gato.getApikey())
                    .build();
            Response response = client.newCall(request).execute();

        }catch (IOException e){
            System.out.println(e);
        }

    }

    public static void verFavoritos(String apiKey) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .method("GET", null)
                .addHeader("x-api-key", apiKey)
                .build();
        Response response = client.newCall(request).execute();

        String elJson = response.body().string();

        //creamos el objeto gson;
        Gson gson = new Gson();

        GatosFav[] gatosArray = gson.fromJson(elJson,GatosFav[].class);

        if(gatosArray.length > 0){
            int min = 1;
            int max = gatosArray.length;
            int aleatorio = (int) Math.random()*((max-min)+1) + min;
            int indice = aleatorio-1;

            GatosFav gatoFav = gatosArray[indice];

            //redimensionar en caso de necesitar
            Image image = null;
            try {
                URL url = new URL(gatoFav.image.getUrl());

                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.addRequestProperty("User-Agent", "");
                BufferedImage bufferedImage = ImageIO.read(http.getInputStream());

                //image = ImageIO.read(url);

                ImageIcon fondoGato = new ImageIcon(bufferedImage);
                if(fondoGato.getIconWidth()>800){
                    //redimensionamos
                    Image fondo = fondoGato.getImage();
                    Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                    fondoGato = new ImageIcon(modificada);
                }
                String menu = "Opciones: \n"+
                        " 1. ver otra imagen \n"+
                        " 2. Eliminar favorito \n"+
                        " 3. volver \n";
                String[] botones = {"ver otra imagen","Eliminar favorito","volver"};
                String id_gato = gatoFav.getId();
                String opcion = (String) JOptionPane.showInputDialog(null,menu,id_gato,
                        JOptionPane.INFORMATION_MESSAGE,fondoGato,
                        botones, botones[0]);

                int seleccion = -1;
                for (int i = 0; i < botones.length; i++) {
                    if(opcion.equals(botones[i])){
                        seleccion = i;
                    }
                }
                switch (seleccion){
                    case 0:
                        verFavoritos(apiKey);
                        break;
                    case 1:
                        borrarFavorito(gatoFav);
                        break;
                    default:
                        break;

                }

            }catch (IOException e){
                System.out.println(e);
            }



        }
    }

    public static void borrarFavorito(GatosFav gatoFav){

        try {

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/"+gatoFav.getId()+"")
                    .method("DELETE", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gatoFav.apiKey)
                    .build();
            Response response = client.newCall(request).execute();

        }catch (IOException e){
            System.out.println(e);

        }

    }

}
