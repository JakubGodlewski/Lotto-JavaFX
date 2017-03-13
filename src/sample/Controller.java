package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private TextField lotto_text_field;

    public Controller()
    {

    }

    @FXML
    void checkNumbers()
    {
        String[] draw_results = checkDrawingResult();
        StringBuilder actualNumber;
        String yourNum = lotto_text_field.getText();

        String yourNumbers[] = new String[6];
        int index = 0;
        boolean correctValue = true;
        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid value", ButtonType.OK);

        if(!yourNum.matches("(\\d{1,2}[ ]){5}\\d{1,2}"))
        {
            alert.show();
            lotto_text_field.setText("");
        }
        else
        {
            actualNumber = new StringBuilder();
            for(int i=0; i<yourNum.length(); i++)
            {
                if(String.valueOf(yourNum.charAt(i)).matches("[0-9]"))
                    actualNumber.append(yourNum.charAt(i));
                else
                {
                    yourNumbers[index++] = actualNumber.toString();
                    actualNumber = new StringBuilder();
                }
                yourNumbers[index] = actualNumber.toString();
            }

            outerloop: for(int i=0; i<yourNumbers.length; i++)
                for(int j=i+1; j<yourNumbers.length; j++)
                    if(yourNumbers[i].equals(yourNumbers[j]))
                    {
                        alert.show();
                        lotto_text_field.setText("");
                        correctValue = false;
                        break outerloop;
                    }

            if(correctValue)
            {
                int hited = 0;

                for(int i=0; i<draw_results.length; i++)
                {
                    for(int j=0; j<yourNumbers.length; j++)
                        if(draw_results[i].equals(yourNumbers[j]))
                            hited++;
                }

                StringBuilder contentText = new StringBuilder();
                contentText.append("Drawing date: "+checkDate()+"\nDrawing results: ");

                for(int i=0; i<draw_results.length; i++)
                {
                    contentText.append(draw_results[i]+" ");
                }

                contentText.append("\nYou hited "+hited+" number");
                if(hited!=1)
                    contentText.append("s");
                alert = new Alert(Alert.AlertType.INFORMATION, contentText.toString(), ButtonType.OK);
                alert.show();
            }
        }
    }

    public String[] checkDrawingResult()
    {
        Matcher matcher = checkWebsite();

        StringBuilder results =new StringBuilder();
        StringBuilder numbers = new StringBuilder();

        Pattern results_pattern = Pattern.compile("<div class=\\\"wynik_lotto\\\">\\d* </div>");
        Pattern numbers_pattern = Pattern.compile("\\d+");
        Matcher results_matcher;
        Matcher numbers_matcher;

        if(matcher.find())
        {
            results_matcher = results_pattern.matcher(matcher.group(0));
            while(results_matcher.find())
            {
                results.append(results_matcher.group(0));
                numbers_matcher = numbers_pattern.matcher(results_matcher.group(0));
                if(numbers_matcher.find())
                    numbers.append(numbers_matcher.group(0)+" ");
            }
        }

        StringBuilder actualNumber = new StringBuilder();

        String draw_results[] = new String[6];
        int draw_index = 0;
        for(int i=0; i<numbers.toString().length(); i++)
        {
            if(String.valueOf(numbers.toString().charAt(i)).matches("[0-9]"))
                actualNumber.append(numbers.toString().charAt(i));
            else
            {
                draw_results[draw_index] = actualNumber.toString();
                actualNumber = new StringBuilder();
                draw_index++;
            }
        }
        return draw_results;
    }

    public String checkDate()
    {
        Matcher matcher = checkWebsite();

        String date ="";
        String hour ="";

        Pattern date_pattern = Pattern.compile("\\d{2}[-]\\d{2}[-]\\d{2}");
        Pattern hour_pattern = Pattern.compile("\\d{2}[:]\\d{2}");

        Matcher date_matcher;
        Matcher hour_matcher;

        if(matcher.find())
        {
            date_matcher = date_pattern.matcher(matcher.group(0));
            if(date_matcher.find())
                date = date_matcher.group(0);
            hour_matcher = hour_pattern.matcher(matcher.group(0));
            if(hour_matcher.find())
                hour = hour_matcher.group(0);
        }

        return date+"; "+hour;
    }

    public Matcher checkWebsite()
    {
        StringBuilder sb = new StringBuilder();
        URL website;
        String inputLine;

        try
        {
            website = new URL("http://www.lotto.pl/");
            URLConnection con = website.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while((inputLine = br.readLine())!=null)
            {
                sb.append(inputLine+'\n');
            }
            br.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Pattern pattern = Pattern.compile("<strong>\\d{2}[-]\\d{2}[-]\\d{2}</strong>\\,"+
                " godz\\.: <strong>\\d{2}:\\d{2}</strong>\\s*</div>\\s*<div class=\\\"glowna_wyniki_lotto\\\">\\s*"+
                "<div class=\\\"wynik_lotto\\\">\\d* </div>\\s*<div class=\\\"wynik_lotto\\\">\\d* </div>\\s*"+
                "<div class=\\\"wynik_lotto\\\">\\d* </div>\\s*<div class=\\\"wynik_lotto\\\">\\d* </div>\\s*"+
                "<div class=\\\"wynik_lotto\\\">\\d* </div>\\s*<div class=\\\"wynik_lotto\\\">\\d* </div>\\s*");
        Matcher matcher = pattern.matcher(sb.toString());
        return matcher;
    }
}
