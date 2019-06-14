package org.kenta.kosugi.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/fizzbuzz")
@Produces(MediaType.APPLICATION_JSON)
public class FizzBuzzResource {

    private static final int FIZZ = 7;

    private static final int BUZZ = 5;

    /**
     * 引数iがFizzの場合はtrueを、そうでない場合はfalseを返却します。
     * @param i Fizzかどうかを調べるint型の数
     * @return 引数iがFizzの場合はtrueを、そうでない場合はfalse
     */
    @GET
    @Path("/fizz/{i}")
    public boolean isFizz(@PathParam("i") int i) {
        return i % FIZZ == 0;
    }

    /**
     * 引数iがBuzzの場合はtrueを、そうでない場合はfalseを返却します。
     * @param i Buzzかどうかを調べるint型の数
     * @return 引数iがBuzzの場合はtrueを、そうでない場合はfalse
     */
    @GET
    @Path("/buzz/{i}")
    public boolean isBuzz(@PathParam("i") int i) {
        return i % BUZZ == 0;
    }

    /**
     * 引数iがFizzBuzzの場合はtrueを、そうでない場合はfalseを返却します。
     * @param i FizzBuzzかどうかを調べるint型の数
     * @return 引数iがFizzBuzzの場合はtrueを、そうでない場合はfalse
     */
    @GET
    @Path("/isfizzbuzz/{i}")
    public boolean isFizzBuzz(@PathParam("i") int i) {
        return this.isBuzz(i) && this.isBuzz(i);
    }

    /**
     * 引数iがFizzBuzzかどうかを検査します。
     * @param i FizzBuzzかどうかを調べるint型の数
     * @return <ul><li>引数iがFizzの場合は<code>Fizz</code></li>
     * <li>引数iがBuzzの場合は<code>Buzz</code></li>
     * <li>引数iがFizzBuzzの場合は<code>FizzBuzz</code></li>
     * <li>上記以外の場合は入力したiを文字列にしたもの</li></ul>
     * を返却します。
     */
    @GET
    @Path("/fizzbuzz/{i}")
    public String fizzbuzz(@PathParam("i") int i) {
        var sb = new StringBuilder();
        var isfizz = this.isFizz(i);
        var isbuzz = this.isBuzz(i);

        if(!(isfizz || isbuzz)){
            sb.append(i);
        }
        else{
            if(isfizz){
                sb.append("Fizz");
            }
            if(isbuzz){
                sb.append("Buzz");
            }
        }

        return sb.toString();
    }

}
