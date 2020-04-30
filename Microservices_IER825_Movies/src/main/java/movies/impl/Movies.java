package movies.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import movies.Movies.Movie;
import movies.Movies.MovieId;
import movies.Movies.MovieIdList;
import movies.Movies.MovieList;

public class Movies implements IMovies{
	private static int id_max = 0;
	private static ArrayList<Movie> filmek = new ArrayList<Movie>();
	private static ArrayList<MovieId> filmekId = new ArrayList<MovieId>();

	@Override
	public MovieList moviesListGet() {
		return MovieList.newBuilder().addAllMovie(filmek).build();
	}

	@Override
	public Movie movieGet(int id) {
		for(int i = 0 ; i < filmekId.size() ; i++) {
			if(filmekId.get(i).getId() == id) {
				return filmek.get(i);
			}
		}
		throw new WebApplicationException(Response.Status.NOT_FOUND);
	}

	@Override
	public MovieId moviePost(Movie movie) {
		id_max++;
		filmek.add(movie);
		filmekId.add(MovieId.newBuilder().setId(id_max).build());
		return MovieId.newBuilder().setId(id_max).build();
	}

	@Override
	public void moviePut(int id, Movie movie) {
		for(int i = 0 ; i < filmek.size() ; i++) {
			if(filmekId.get(i).getId() == id) {
				filmek.remove(i);
				filmekId.remove(i);
			}
		}
		if(id>id_max) {
			id_max = id;
		}
		filmek.add(movie);
		filmekId.add(MovieId.newBuilder().setId(id).build());
	}

	@Override
	public MovieIdList find(int year, String field) {
		ArrayList<Integer> tempID = new ArrayList<>();
		if(field.equals("Title")) {
			Collections.sort(filmek, Comparator.comparing(s -> s.getTitle()));
		}else if(field.equals("Director")){
			Collections.sort(filmek, Comparator.comparing(s -> s.getDirector()));
		}
		
		for(int i = 0 ; i < filmek.size() ; i++) {
			if(filmek.get(i).getYear() == year) {
				tempID.add(filmekId.get(i).getId());
			}
		}
		return MovieIdList.newBuilder().addAllId(tempID).build();
	}

	@Override
	public void movieDelete(int id) {
		for(int i = 0 ; i < filmekId.size() ; i++) {
			if(filmekId.get(i).getId() == id) {
				filmek.remove(i);
				filmekId.remove(i);
			}
		}
	}

}

