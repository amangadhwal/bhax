#version 300 es

// Vertex attributes
layout(location = 0) in vec4 aPosition;
layout(location = 1) in vec2 aTexCoord;

// Output to fragment shader
out vec2 vTextureCoord;

// Uniforms for transformation
uniform mat4 uMVPMatrix;
uniform float uTime;
uniform float uDistortionAmount;
uniform vec2 uDistortionCenter;

// Constants for vertex displacement
const float PI = 3.14159265359;
const float WAVE_SPEED = 2.0;
const float WAVE_AMPLITUDE = 0.02;
const int NUM_WAVES = 3;

// Wave distortion function
vec4 applyWaveDistortion(vec4 position) {
    vec2 toCenter = position.xy - uDistortionCenter;
    float dist = length(toCenter);
    float angle = atan(toCenter.y, toCenter.x);
    
    // Multiple wave frequencies
    float displacement = 0.0;
    for(int i = 0; i < NUM_WAVES; i++) {
        float frequency = float(i + 1);
        displacement += sin(dist * frequency - uTime * WAVE_SPEED) 
            * WAVE_AMPLITUDE / frequency;
    }
    
    // Apply radial displacement
    float distortionFactor = displacement * uDistortionAmount;
    position.x += cos(angle) * distortionFactor;
    position.y += sin(angle) * distortionFactor;
    
    return position;
}

// Vertex deformation based on noise
vec4 applyNoiseDeformation(vec4 position) {
    float noiseTime = uTime * 0.5;
    float noiseX = sin(position.x * 10.0 + noiseTime) * uDistortionAmount * 0.02;
    float noiseY = cos(position.y * 10.0 + noiseTime) * uDistortionAmount * 0.02;
    
    position.x += noiseX;
    position.y += noiseY;
    
    return position;
}

void main() {
    // Start with original position
    vec4 position = aPosition;
    
    // Apply wave distortion if distortion amount is > 0
    if (uDistortionAmount > 0.0) {
        position = applyWaveDistortion(position);
        position = applyNoiseDeformation(position);
    }
    
    // Transform vertex position
    gl_Position = uMVPMatrix * position;
    
    // Pass texture coordinates to fragment shader
    vTextureCoord = aTexCoord;
    
    // Add slight perspective warping based on time
    float warpAmount = sin(uTime * 0.5) * 0.01;
    vTextureCoord += vec2(warpAmount * gl_Position.y, warpAmount * gl_Position.x);
}
