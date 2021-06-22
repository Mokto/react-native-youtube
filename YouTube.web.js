import React from 'react';

const YouTube = ({ videoId, style }) => {
  return (
    <iframe
      title="PMU"
      src={`https://www.youtube.com/embed/${videoId}`}
      style={style}
      frameBorder="0"
      allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen
    />
  );
};

export default YouTube;
