
attribute vec4 inPosition;
attribute vec2 inTexCoord;

varying vec2 texCoord;
uniform int m_Eye;

void main() {     
    vec2 pos = inPosition.xy * 2.0 - 1.0;
    pos.x *= 0.5;
    if(m_Eye == 0){
        pos.x -= 0.5;
    } else {
       pos.x += 0.5; 
    }
    gl_Position = vec4(pos, 0.0, 1.0);    
    texCoord = inTexCoord;
}