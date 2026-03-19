function submitQuiz(){

let score = Math.floor(Math.random() * 10) + 1;

let level;
let message;

if(score <= 4){
level = "Low";
message = "Don't worry! Keep learning and try again.";
}
else if(score <= 7){
level = "Medium";
message = "Good job! With more practice you can improve.";
}
else{
level = "High";
message = "Excellent! You have strong knowledge.";
}

document.getElementById("result").innerHTML =
"Score : " + score + "/10 <br>" +
"Level : " + level + "<br>" +
message;

}