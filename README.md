# StepCounter - Prototype (Android)

Hi! This is a demo project that retrieves data from Google Fit for the past 7 days from the current date.

## Project Details
 - Written in Kotlin
 - Architecture : Model-View-Presenter
 - UI is built using ConstraintLayout
 - Users can signin using Google
 - Basic user database on Google Firebase
 - No health information is uploaded to server

## Demo Video

![Demo Video](https://i.imgur.com/R6l7JJg.gif)

## App Flow

 - User can login using Google Sign In
 - Allow access to Google Fit
	 - On success, the step count details for the past 7 days are shown. Current daily step count target is set to 4000. Depending on the percentage of steps completed, different emojis are displayed
	 - Current day's steps is highlighted in a different color and is only shown if any data is available.
	 - On failure to access Google Fit, error message is displayed
 - User can logout by clicking on the profile picture on the top right

## To-Dos

 - Use Dependency Injection
 - Add Unit Tests

## Libraries Used

 - Google Firebase
 - Glide
 - ShapeView


