// More API functions here:
// https://github.com/googlecreativelab/teachablemachine-community/tree/master/libraries/image

// the link to your model provided by Teachable Machine export panel
const URL = "./my_model/";

let model, webcam, labelContainer, maxPredictions;
let initRunning = false;
let webcamLoaded = false;

// Load the image model and setup the webcam
async function init() {
	if(initRunning) return
	initRunning = true;
	
    const modelURL = URL + "model.json";
    const metadataURL = URL + "metadata.json";

    // load the model and metadata
    // Refer to tmImage.loadFromFiles() in the API to support files from a file picker
    // or files from your local hard drive
    // Note: the pose library adds "tmImage" object to your window (window.tmImage)
    model = await tmImage.load(modelURL, metadataURL);
    maxPredictions = model.getTotalClasses();

    // Convenience function to setup a webcam
    const flip = true; // whether to flip the webcam
    webcam = new tmImage.Webcam(200, 200, flip); // width, height, flip
    await webcam.setup(); // request access to the webcam
    await webcam.play();
    window.requestAnimationFrame(loop);

    // append elements to the DOM
    document.getElementById("webcam-container").appendChild(webcam.canvas);
    labelContainer = document.getElementById("label-container");
    for (let i = 0; i < maxPredictions; i++) { // and class labels
        labelContainer.appendChild(document.createElement("div"));
    }
    webcamLoaded = true;
}

async function loop() {
    webcam.update(); // update the webcam frame
    await predict();
    window.requestAnimationFrame(loop);
}

// run the webcam image through the image model
async function predict() {
    // predict can take in an image, video or canvas html element
    const prediction = await model.predict(webcam.canvas);
    for (let i = 0; i < maxPredictions; i++) {
        const classPrediction =
            prediction[i].className + ": " + prediction[i].probability.toFixed(2);
        labelContainer.childNodes[i].innerHTML = classPrediction;
    }
}

function showYourScore(){
	if(initRunning && webcamLoaded){
		const badScore = document.querySelector('#label-container > div:nth-child(2)');
    	let percentBadScore = badScore.textContent.slice(-2);
    	
    	//null이니 """이니 체크를 이렇게 하는구나
    	if(! percentBadScore){
    		return;
    	}
    	
    	if(percentBadScore.charAt(0)=='0'){
    		percentBadScore = percentBadScore.slice(1);
    	}
    	
    	if(percentBadScore==0){
    		alert('Wow ! 당신의 외모는 완벽합니다 !');
    	}else{
    		alert('당신 외모의 '+percentBadScore+'% 영역이 못생겼습니다 !');	
    	}
    	
	}else{
		alert('먼저 거울을 꺼내주세요 !');
	}
	
}

function exit(){
	
	if(initRunning && webcamLoaded){
		window.location.href = 'face';	
	}else{
		alert('먼저 거울을 꺼내주세요 !');
	}
	
}